package com.wmsi.sgx.service.sandp.capiq.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.service.indexer.IndexerService;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CompanyCSVRecord;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class FinancialsService extends AbstractDataService {

	@Autowired
	private IndexerService indexerService;
	
	private static final Logger log = LoggerFactory.getLogger(FinancialsService.class);
	
	@Value("${loader.company-data.dir}")
	private String companyDataDir;
	
	/**
	 * Load Financials data based on company ticker 
	 * @param company ticker
	 * @return Financials
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override	
	public Financials load(String id, String... parms) throws ResponseParserException, CapIQRequestException {
		Assert.notEmpty(parms);
		Financials financial = getCompanyFinancials(id, parms[0]);
		return financial;
	}
	
	/**
	 * Load Financials data based on company ticker 
	 * @param company ticker
	 * @return Financials
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public Financials getCompanyFinancials(String id, String currency) throws ResponseParserException, CapIQRequestException {		
		String tickerNoEx = id.split(":")[0];
		Financials financials = new Financials();
		financials.setFinancials(new ArrayList<Financial>());
		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
		//CSVHelperUtil csvHelperUtil = new CSVHelperUtil(); 
		Map<String, List<CompanyCSVRecord>> dataMap = getMap(tickerNoEx, getParsedFinacnialRecords(id, companyDataDir));		

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
