package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVRecord;
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
		CSVHelperUtil csvHelperUtil = new CSVHelperUtil(); 
		Map<String, List<CSVRecord>> dataMap = csvHelperUtil.getMap(tickerNoEx, getCompanyData(id, "company-data"));		

		Iterator<Entry<String, List<CSVRecord>>> i = dataMap.entrySet().iterator();
		
		while(i.hasNext()){
			Entry<String, List<CSVRecord>> entry = i.next();
			Iterable<CSVRecord> records = entry.getValue();
			Map<String, Object> financialMap = new HashMap<String, Object>();
			financialMap.put("absPeriod", entry.getKey());
			financialMap.put("tickerCode", tickerNoEx);
			for (CSVRecord record : records) {				
				financialMap.put(record.get(2), record.get(3));				
			}
			JsonElement jsonElement = gson.toJsonTree(financialMap);
			Financial fin = gson.fromJson(jsonElement, Financial.class);
			List<Financial> list = financials.getFinancials();
			list.add(fin);			
			financials.setFinancials(list);
		}
		
		return financials;
	}
}
