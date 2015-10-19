package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVRecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class EstimatesService extends AbstractDataService{

	
	@Override
	public Estimates load(String id, String... parms) throws ResponseParserException, CapIQRequestException {
		id=id.split(":")[0];
		return getEstimates(id);
	}
	
	private Estimates getEstimates(String id){
		Estimates estimates = new Estimates();
		estimates.setEstimates(new ArrayList<Estimate>());
		//String file = "src/main/resources/data/consensus-estimates.csv";
		String file = "/mnt/sgx-data/consensus-estimates.csv";
		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
		CSVHelperUtil csvHelperUtil = new CSVHelperUtil();
		Map<String, List<CSVRecord>> dataMap = getMap(id, csvHelperUtil.getRecords(file));		
		System.out.println(dataMap.keySet());
		Iterator<Entry<String, List<CSVRecord>>> i = dataMap.entrySet().iterator();
		
		while(i.hasNext()){
			Entry<String, List<CSVRecord>> entry = i.next();
			Iterable<CSVRecord> records = entry.getValue();
			Map<String, Object> estimateMap = new HashMap<String, Object>();
			estimateMap.put("period", entry.getKey());
			estimateMap.put("tickerCode", id);
			for (CSVRecord record : records) {				
				estimateMap.put(record.get(2), record.get(3));				
			}
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
			//if(record.get(0).equalsIgnoreCase(ticker) &&  (record.get(4) != null) && !record.get(4).isEmpty()){
				if(record.get(0).equalsIgnoreCase(ticker) ){
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
