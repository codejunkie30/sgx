package com.wmsi.sgx.service.search.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.search.Criteria;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.service.conversion.ModelMapper;
import com.wmsi.sgx.service.search.SearchResult;

/**
 * Manually filter search results by percent change. This is done locally in java code rather than via elasticsearch query 
 * due to the complexity of calculating percent change within results between dates dynamically. It can be done in elasticsearch 
 * via a combination of script queries and filters however in practice that proved to be too slow.   
 */
@Component
public class PercentChangeFilter implements Filter<SearchCompany, Company>{

	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public List<SearchCompany> filter(SearchResult<Company> results, List<Criteria> criteria) throws FilterException{
		
		Criteria percentChangeCriteria = null;
		
		for(Criteria c : criteria){
			if(c.getField().equals("percentChange")){
				percentChangeCriteria = c;
				break;
			}
		}

		List<SearchCompany> searchCompanies = null; 
		
		if(percentChangeCriteria != null){
			// Apply filter matches for percent change
			searchCompanies = percentChangeFilter(results, percentChangeCriteria);
		}
		else{
			searchCompanies = modelMapper.mapList(results.getHits(), SearchCompany.class);
		}
		
		return searchCompanies;
	}
	
	private List<SearchCompany> percentChangeFilter(SearchResult<Company> result, Criteria percentChangeCriteria) throws FilterException{
		List<SearchCompany> ret = new ArrayList<SearchCompany>();
		
		for(Company comp : result.getHits()){
	
			List<HistoricalValue> hist = getHistoryBetween(comp, percentChangeCriteria);
			
			if(hist.size() > 1){
				
				double first = hist.get(0).getValue();
				double last = hist.get(hist.size() - 1).getValue();
				
				double change = (last - first) * 100;

				// Round down and ignore sign then compare to search criteria
				if(Math.floor(Math.abs(change)) == Double.parseDouble(percentChangeCriteria.getValue())){
					SearchCompany s = (SearchCompany) modelMapper.map(comp, SearchCompany.class);
					s.setPercentChange(change);
					ret.add(s);
				}
			}
		}
		
		return ret;
	}

	private List<HistoricalValue> getHistoryBetween(Company company, Criteria percentChangeCriteria) throws FilterException{
		
		List<HistoricalValue> hist = new ArrayList<HistoricalValue>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date fromDate, toDate = null;
		
		try{
			fromDate = sdf.parse(percentChangeCriteria.getFrom().toString());
			toDate = sdf.parse(percentChangeCriteria.getTo().toString());
		}
		catch(ParseException e){
			throw new FilterException("Failed to parse dates from criteria", e);
		}
		
		for(HistoricalValue h : company.getPriceHistory()){
			if(h.getDate().compareTo(fromDate) >= 0 &&  h.getDate().compareTo(toDate) <= 0)
				hist.add(h);
		}
		
		Collections.sort(hist, new Comparator<HistoricalValue>(){
			@Override
			public int compare(HistoricalValue o1, HistoricalValue o2) {
				return o1.getDate().compareTo(o2.getDate());
			}			
		});
		
		return hist;
	}

}
