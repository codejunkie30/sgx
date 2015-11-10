package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class FinancialsService extends AbstractDataService {

	
	@Override	
	public Financials load(String id, String... parms) throws ResponseParserException, CapIQRequestException {
		Assert.notEmpty(parms);
		Financials financial = getCompanyFinancials(id, parms[0]);
		return financial;
	}
	
	public Financials getCompanyFinancials(String id, String currency) throws ResponseParserException, CapIQRequestException {		
		String tickerNoEx = id.split(":")[0];
		Financials financials = new Financials();
		financials.setFinancials(new ArrayList<Financial>());
		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
		//CSVHelperUtil csvHelperUtil = new CSVHelperUtil(); 
		Map<String, List<CSVRecord>> dataMap = getMap(tickerNoEx, getCompanyData(id, "company-data"));		

		Iterator<Entry<String, List<CSVRecord>>> i = dataMap.entrySet().iterator();
		
		while(i.hasNext()){
			Entry<String, List<CSVRecord>> entry = i.next();
			Iterable<CSVRecord> records = entry.getValue();
			Map<String, Object> financialMap = new HashMap<String, Object>();
			financialMap.put("absPeriod", entry.getKey());
			financialMap.put("tickerCode", tickerNoEx);
			Date period = null;
			for (CSVRecord record : records) {				
				if (StringUtils.stripToNull(record.get(3)) == null) continue;
				financialMap.put(record.get(2), record.get(3));
				if (period == null && StringUtils.stripToNull(record.get(5)) != null)
					period = new Date(record.get(5));
			}
			if (period != null) 
				financialMap.put("periodDate", period);
			JsonElement jsonElement = gson.toJsonTree(financialMap);
			financialMap.put("period", tickerNoEx);
			Financial fin = gson.fromJson(jsonElement, Financial.class);
			processMillionRangeValues(fin);
			List<Financial> list = financials.getFinancials();
			list.add(fin);			
			financials.setFinancials(list);
		}
		
		return financials;
	}
	
	public Map<String, List<CSVRecord> > getMap(String ticker, Iterable<CSVRecord> records){
		Map<String, List<CSVRecord>> map = new HashMap<String, List<CSVRecord>>();
		for (CSVRecord record : records) {
			if(record.get(0).equalsIgnoreCase(ticker) &&  (record.get(4) != null) && !record.get(4).isEmpty()){
 				String key = record.get(4);
				List<CSVRecord> r = null;
				
				if(map.containsKey(key)){
					r = map.get(key);					
				}else{
					r = new ArrayList<CSVRecord>();
					map.put(key, r);
				}
				
				r.add(record);
			}
		}
		
		return map;		
	}
}
