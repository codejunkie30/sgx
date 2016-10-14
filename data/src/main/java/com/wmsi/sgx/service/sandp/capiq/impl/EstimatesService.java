package com.wmsi.sgx.service.sandp.capiq.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CompanyCSVRecord;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class EstimatesService extends AbstractDataService{
	private static final Logger log = LoggerFactory.getLogger(EstimatesService.class);
	
	@Value("${loader.consensus-estimates.dir}")
	private String consensusEstimatesDir;
	
	/**
	 * Load Estimates based on company ticker 
	 * @param company ticker
	 * @return Estimates
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override
	public Estimates load(String id, String... parms) throws ResponseParserException, CapIQRequestException {
		return getEstimates(id);
	}
	
	/**
	 * Get Estimates based on company ticker 
	 * @param company ticker
	 * @return Estimates
	 */
	private Estimates getEstimates(String id){
		String tickerNoEx = id.split(":")[0];
		Estimates estimates = new Estimates();
		estimates.setEstimates(new ArrayList<Estimate>());
		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
		
		List<CompanyCSVRecord> all = null;
		try { all = getParsedFinacnialRecords(id, consensusEstimatesDir); }
		catch (Exception e) {}

		// don't need estimates
		if (all == null) return estimates;
		
		Map<String, List<CompanyCSVRecord>> dataMap = getMap(tickerNoEx, all);		
		Iterator<Entry<String, List<CompanyCSVRecord>>> i = dataMap.entrySet().iterator();
		
		while(i.hasNext()){
			
			Entry<String, List<CompanyCSVRecord>> entry = i.next();
			List<CompanyCSVRecord> records = entry.getValue();
			Map<String, Object> estimateMap = new HashMap<String, Object>();
			String period = entry.getKey();
			
			if(isValidPeriod(period)){
				estimateMap.put("period", entry.getKey());
				estimateMap.put("tickerCode", tickerNoEx);
				Date periodEndDate = null;
				Field[] fields = Estimate.class.getDeclaredFields();
				
				for (Field field : fields) {
					String name = field.getName();
					try {
						Object val = getFieldValue(field, records);
						if (val == null) val = getFieldDate(field, records);
						if (val != null) estimateMap.put(name, val);
					} catch (Exception e) {
						log.error("Getting field val: " + field.getName(), e);
					}
				}
				
				for (CompanyCSVRecord record : records) {	
					if (periodEndDate == null && record.getPeriodEndDate() != null) periodEndDate = record.getPeriodEndDate();
				}
				if (periodEndDate != null) estimateMap.put("periodDate", periodEndDate);
				JsonElement jsonElement = gson.toJsonTree(estimateMap);
				Estimate est = gson.fromJson(jsonElement, Estimate.class);
				List<Estimate> list = estimates.getEstimates();
				list.add(est);
				estimates.setEstimates(list);
			}
		}
		
		return estimates;
	}
	
	public Map<String, List<CompanyCSVRecord> > getMap(String ticker, List<CompanyCSVRecord> records){
		Map<String, List<CompanyCSVRecord>> map = new HashMap<String, List<CompanyCSVRecord>>();
		for (CompanyCSVRecord record : records) {
			//if(record.getTicker().equalsIgnoreCase(ticker) &&  (record.getPeriod() != null) && !record.getPeriod().isEmpty()){
			String key;
			if(record.getPeriod() == null){
				key = "";
			}else
				key = record.getPeriod();
			List<CompanyCSVRecord> r = null;
			if(map.containsKey(key)) r = map.get(key);					
			else {
				r = new ArrayList<CompanyCSVRecord>();
				map.put(key, r);
			}
				
			r.add(record);
			
		}
		return map;		
	}
	
	/**
	 * Check if period is valid for valid Estimates
	 * based on requirements
	 * Estimates needs to be calculated only for present, previous and next year only  
	 * @param period to be verified 
	 * @return Boolean
	 */
	public Boolean isValidPeriod(String period){
		
		if (period == ""){
			return true;
		}else{
			
			Calendar prevYear = Calendar.getInstance();
			prevYear.add(Calendar.YEAR, -1);
			int lastYear = prevYear.get(Calendar.YEAR);
			
			
			Calendar nextYear = Calendar.getInstance();
			nextYear.add(Calendar.YEAR, 1);
			int currentYearPlusOne = nextYear.get(Calendar.YEAR);
			
			try{
				int yearFromPeriod = Integer.parseInt(period.substring(period.length()-4));
				//Checking for one year previous or one year ahead
				if(yearFromPeriod >=lastYear && yearFromPeriod <= currentYearPlusOne){
					return true;
				}
			}catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}
}
