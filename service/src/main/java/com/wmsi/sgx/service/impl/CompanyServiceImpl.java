package com.wmsi.sgx.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.search.Search;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@Service
public class CompanyServiceImpl implements CompanyService{

	@Autowired
	private SearchService companySearchService;

	@Override
	public CompanyInfo getById(String id, Class<CompanyInfo> clz) throws CompanyServiceException {
		try{
			return companySearchService.getById(id, clz);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Could not load company by id", e);
		}
	}
	
	@Autowired
	private Search<KeyDevs> keyDevsSearch;

	@Override
	public KeyDevs loadKeyDevs(String id) throws CompanyServiceException{
		List<KeyDevs> hits = search(keyDevsSearch, id);
		return hits != null && hits.size() > 0 ? hits.get(0) : null;
	}
	
	@Autowired
	private Search<Holders> holdersSearch;
	
	public Holders loadHolders(String id) throws CompanyServiceException{
		List<Holders> hits = search(holdersSearch, id);		
		return hits != null && hits.size() > 0 ? hits.get(0) : null;
	}
	
	@Autowired
	private Search<CompanyFinancial> financialSearch;
	
	@Override
	public List<CompanyFinancial> loadFinancials(String id) throws CompanyServiceException{
		return search(financialSearch, id);
	}

	@Autowired
	private Search<HistoricalValue> priceSearch;
	
	@Override
	public List<HistoricalValue> loadPriceHistory(String id) throws CompanyServiceException{
		return search(priceSearch, id);
	}

	@Autowired
	private Search<HistoricalValue> volumeSearch;
	
	@Override
	public List<HistoricalValue> loadVolumeHistory(String id) throws CompanyServiceException{
		return search(volumeSearch, id);
	}
	
	@Autowired
	private Search<AlphaFactor>	alphaFactorSearch;
	
	@Override
	public AlphaFactor loadAlphaFactors(String id) throws CompanyServiceException{
		
		List<AlphaFactor> hits = null;
		CompanyInfo info = getById(id, CompanyInfo.class);
		
		if(info != null){
			String gvKey = info.getGvKey().substring(3); // Trim 'GV_' prefix from actual id
			hits = search(alphaFactorSearch, gvKey);
		}
		
		return hits != null && hits.size() > 0 ? hits.get(0) : null;		
	}
	
	@Autowired
	private Search<CompanyInfo> relatedCompaniesSearch;
	
	@Override
	public List<CompanyInfo> loadRelatedCompanies(String id) throws CompanyServiceException{
		
		CompanyInfo company = getById(id, CompanyInfo.class);
		
		List<CompanyInfo> companies = null;
		
		if(company != null && company.getMarketCap() != null){
			
			Map<String, Object> parms = new HashMap<String, Object>();
			parms.put("tickerCode", company.getTickerCode());
			parms.put("industry", company.getIndustry());
			parms.put("industryGroup", company.getIndustryGroup());
			parms.put("field", "marketCap");
			parms.put("fieldValue", company.getMarketCap());
			parms.put("percent", 0.1);
			
			try{
				companies = companySearchService.search(relatedCompaniesSearch, parms);
			}
			catch(SearchServiceException e){
				throw new CompanyServiceException("Could not load related companies", e);
			}
		}
		
		return companies;
	}
	
	private <T> List<T> search(Search<T> s, String id) throws CompanyServiceException{
		try{
			return companySearchService.search(s, getParms(id));
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Error loading company data.", e);
		}
	}
	
	private Map<String, Object> getParms(String id){
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("id", id);
		return parms;
	}
}
