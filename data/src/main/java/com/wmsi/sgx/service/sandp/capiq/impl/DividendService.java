package com.wmsi.sgx.service.sandp.capiq.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.DividendValue;
import com.wmsi.sgx.service.indexer.IndexerServiceException;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CompanyCSVRecord;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
public class DividendService extends AbstractDataService{
	
	@Value("${loader.dividend-history.dir}")
	private String dividendHistoryDir;
	
	/**
	 * Load Dividend History based on company ticker 
	 * @param company ticker
	 * @return DividendHistory
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@SuppressWarnings("unchecked")
	public DividendHistory load(String id, String... parms)
			throws ResponseParserException, CapIQRequestException {
		
		Assert.notEmpty(parms);

		try {
			return getDividendData(id);
		}
		catch(Exception e) {
			throw new ResponseParserException("loading dividends for " + id, e);
		}
		
	}
	/**
	 * Get Dividend data based on company ticker 
	 * @param company ticker
	 * @return DividendHistory
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 * @throws IndexerServiceException
	 */
	public DividendHistory getDividendData(String id) throws ResponseParserException, CapIQRequestException, IndexerServiceException {		
		DividendHistory dH = new DividendHistory();
		dH.setTickerCode(id);		
		
		Iterable<CSVRecord> records = null; 
		try { records = getCompanyData(id, dividendHistoryDir); }
		catch(Exception e) {}
		
		// don't need a history
		if (records == null) return dH;
		
		List<DividendValue> list = new ArrayList<DividendValue>();
		
		Field field = null;
		try { field = DividendValue.class.getDeclaredField("dividendPrice"); }
		catch(Exception e) {}
		
				
		for (CSVRecord record : records) {
			CompanyCSVRecord csr = new CompanyCSVRecord(record.get(6), record.get(4), new Date(record.get(2)));
			String val = getFieldValue(field, csr);
			DividendValue dV = new DividendValue();
			dV.setDividendExDate(csr.getPeriodDate());
			if (StringUtils.stripToNull(record.get(3)) != null) dV.setDividendPayDate(new Date(record.get(3)));
			if (val != null) dV.setDividendPrice(Double.parseDouble(val));
			if (StringUtils.stripToNull(record.get(5)) != null) dV.setDividendType(record.get(5));				
	    	list.add(dV);		    	
		}
		
		if (!list.isEmpty()) {
			Collections.sort(list, DividendValue.DividendValueDateComparator);
			if (list.size() >= 10) list.subList(0, 9);
		}
		
		dH.setDividendValues(list);
		
		return dH;
	}
	
	
}