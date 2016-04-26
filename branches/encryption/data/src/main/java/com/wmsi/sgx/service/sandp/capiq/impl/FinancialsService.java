package com.wmsi.sgx.service.sandp.capiq.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wmsi.sgx.model.FXRecord;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.annotation.FXAnnotation;
import com.wmsi.sgx.service.indexer.IndexerService;
import com.wmsi.sgx.service.indexer.IndexerServiceException;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CompanyCSVRecord;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class FinancialsService extends AbstractDataService {

	@Autowired
	private IndexerService indexerService;
	
	private static final Logger log = LoggerFactory.getLogger(FinancialsService.class);
	
	
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
		Map<String, List<CompanyCSVRecord>> dataMap = getMap(tickerNoEx, getParsedFinacnialRecords(id, "company-data"));		

		Iterator<Entry<String, List<CompanyCSVRecord>>> i = dataMap.entrySet().iterator();
		
		while(i.hasNext()){
			Entry<String, List<CompanyCSVRecord>> entry = i.next();
			List<CompanyCSVRecord> records = entry.getValue();
			Map<String, Object> financialMap = new HashMap<String, Object>();
			financialMap.put("absPeriod", entry.getKey());
			financialMap.put("tickerCode", tickerNoEx);
			
			Date periodEndDate = null;
			
			Field[] fields = Financial.class.getDeclaredFields();
		//	Map<String, Object> fieldMapValue = new HashMap<String, Object>();

			for (Field field : fields) {
				String name = field.getName();
				try {
					Object val = getFieldValue(field, records);
					if (val == null) val = getFieldDate(field, records);
					if (val != null) financialMap.put(name, val);
				} catch (Exception e) {
					log.error("Getting field val: " + field.getName(), e);
				}
			}
			
			for (CompanyCSVRecord record : records) {	
				if (periodEndDate == null && record.getPeriodEndDate() != null) 
					periodEndDate = record.getPeriodEndDate();
			}
			if (periodEndDate != null) 
				financialMap.put("periodDate", periodEndDate);
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
	
	
	/*
	*//**
	 * get the value for a particular field
	 * @param name
	 * @param records
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 *//*
	public String getFieldValue(Field field, List<CompanyCSVRecord> records) throws ResponseParserException, CapIQRequestException, IndexerServiceException {
		
		CompanyCSVRecord actual = null;
		
		for (CompanyCSVRecord record : records) {
			if (!record.getName().equals(field.getName())) continue;
			if (actual == null || record.getPeriodDate() == null || record.getPeriodDate().after(actual.getPeriodDate())) actual = record;
		}
		
		return getFieldValue(field, actual);
	}
	
	*//**
	 * get the value for a particular field
	 * @param name
	 * @param record
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 *//*
	public String getFieldValue(Field field, CompanyCSVRecord actual) throws ResponseParserException, CapIQRequestException, IndexerServiceException {
		String value = actual == null ? null : StringUtils.stripToNull(actual.getValue());

		// nothing to do
		if (value == null) return null;
		
		// fx convert
		if (field.isAnnotationPresent(FXAnnotation.class)) value = getFXConverted(field, value, actual, indexerService.getIndexName());
		
		return value;
	}
	
	public String getFXConverted(Field field, String value, CompanyCSVRecord actual, String indexName) throws ResponseParserException, CapIQRequestException, IndexerServiceException {
		String dataTobeConvertedInCurrency = indexName.substring(0,3).toUpperCase();
		// can't convert
		if (value == null || actual.getCurrency() == null || actual.getPeriodDate() == null){ 
			return value;
		}
		
		if (!FXRecord.shouldConvert(actual.getCurrency(), indexName)) {
			return actual.getValue();
		}

		// pull it from the cache
		FXRecord record = FXRecord.getFromCache(actual.getCurrency(), dataTobeConvertedInCurrency, actual.getPeriodDate());
		
		// offtime runs, might get future dates we can't convert grab the latest we have
		if (record == null) record = FXRecord.getLatestRate(actual.getCurrency(), dataTobeConvertedInCurrency);
		
		// get the value
		if (record != null) {
			BigDecimal val = BigDecimal.valueOf(record.getMultiplier()).multiply(new BigDecimal(actual.getValue()));
			return val.toString();
		}
		
		throw new ResponseParserException("No conversion available for field " + actual);
		
	}*/
	
	public Map<String, List<CompanyCSVRecord> > getMap(String ticker, List<CompanyCSVRecord> records){
		Map<String, List<CompanyCSVRecord>> map = new HashMap<String, List<CompanyCSVRecord>>();
		for (CompanyCSVRecord record : records) {
			if(record.getTicker().equalsIgnoreCase(ticker) &&  (record.getPeriod() != null) && !record.getPeriod().isEmpty()){
			//if(record.getTicker().equalsIgnoreCase(ticker)){
 				String key = record.getPeriod();
				List<CompanyCSVRecord> r = null;
				
				if(map.containsKey(key)){
					r = map.get(key);					
				}else{
					r = new ArrayList<CompanyCSVRecord>();
					map.put(key, r);
				}
				
				r.add(record);
			}
		}	
		return map;		
	}
}
