package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.springframework.util.Assert;

import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.DividendValue;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
public class DividendService extends AbstractDataService{
	
	@SuppressWarnings("unchecked")
	public DividendHistory load(String id, String... parms)
			throws ResponseParserException, CapIQRequestException {
		Assert.notEmpty(parms);
		return getDividendData(id);
	}
	
	public DividendHistory getDividendData(String id) throws ResponseParserException, CapIQRequestException {		
		DividendHistory dH = new DividendHistory();
		dH.setTickerCode(id);		
		
		Iterable<CSVRecord> records = null; 
		try { getCompanyData(id, "dividend-history"); }
		catch(Exception e) {}
		
		// don't need a history
		if (records == null) return dH;
		
		List<DividendValue> list = new ArrayList<DividendValue>();
				
		for (CSVRecord record : records) {
			DividendValue dV = new DividendValue();
			dV.setDividendExDate(new Date(record.get(2)));
			dV.setDividendPayDate(new Date(record.get(3)));
			dV.setDividendPrice(Double.parseDouble(record.get(4)));
			dV.setDividendType(record.get(5));				
	    	list.add(dV);		    	
		}
		
		dH.setDividendValues(list);
		
		return dH;
	}
	
	
}