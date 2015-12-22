package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Date;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class EstimatesService extends AbstractDataService{

	
	@Override
	public Estimates load(String id, String... parms) throws ResponseParserException, CapIQRequestException {
		return getEstimates(id);
	}
	
	private Estimates getEstimates(String id){
		String tickerNoEx = id.split(":")[0];
		Estimates estimates = new Estimates();
		estimates.setEstimates(new ArrayList<Estimate>());
		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
		
		Iterable<CSVRecord> all = null;
		try { all = getCompanyData(id, "consensus-estimates"); }
		catch (Exception e) {}

		// don't need estimates
		if (all == null) return estimates;
		
		Map<String, List<CSVRecord>> dataMap = getMap(tickerNoEx, all);		
		Iterator<Entry<String, List<CSVRecord>>> i = dataMap.entrySet().iterator();
		
		while(i.hasNext()){
			Entry<String, List<CSVRecord>> entry = i.next();
			Iterable<CSVRecord> records = entry.getValue();
			Map<String, Object> estimateMap = new HashMap<String, Object>();
			estimateMap.put("period", entry.getKey());
			estimateMap.put("tickerCode", tickerNoEx);
			Date periodDate = null;
			for (CSVRecord record : records) {	
				estimateMap.put(record.get(2), Double.parseDouble(record.get(3)));	
				if (periodDate == null && StringUtils.stripToNull(record.get(5)) != null) periodDate = new Date(record.get(5));
			}
			if (periodDate != null) estimateMap.put("periodDate", periodDate);
			JsonElement jsonElement = gson.toJsonTree(estimateMap);
			Estimate est = gson.fromJson(jsonElement, Estimate.class);
			List<Estimate> list = estimates.getEstimates();
			list.add(est);
			estimates.setEstimates(list);
		}
		
		return estimates;
	}
	
	public Map<String, List<CSVRecord> > getMap(String ticker, Iterable<CSVRecord> records){
		Map<String, List<CSVRecord>> map = new HashMap<String, List<CSVRecord>>();
		for (CSVRecord record : records) {
			String key = record.get(4);
			List<CSVRecord> r = null;
			if(map.containsKey(key)) r = map.get(key);					
			else {
				r = new ArrayList<CSVRecord>();
				map.put(key, r);
			}
				
			r.add(record);
		}
		return map;		
	}
}