package com.wmsi.sgx.service.sandp.capiq.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;

import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;

@SuppressWarnings("unchecked")
public class HistoricalService extends AbstractDataService {
	
	@Override
	public PriceHistory load(String id, String... parms) throws CapIQRequestException, ResponseParserException {
		Assert.notEmpty(parms);		
		return getHistoricalData(id);
	}
	
	public PriceHistory getHistoricalData(String id) throws ResponseParserException, CapIQRequestException {
		String tickerNoEx = id.split(":")[0];
		PriceHistory ph = new PriceHistory();
		Iterable<CSVRecord> records = getCompanyData(id, "company-data");
		List<String> list = Arrays.asList("openPrice", "closePrice", "volume", "highPrice", "lowPrice");
		
		List<HistoricalValue> price = new ArrayList<HistoricalValue>();
		List<HistoricalValue> highPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> lowPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> openPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> volume = new ArrayList<HistoricalValue>();
		
		for (CSVRecord record : records) {
			if(list.contains(record.get(2))){
				HistoricalValue hv = new HistoricalValue();
				hv.setTickerCode(tickerNoEx);								
				
				Double value  = !record.get(3).equalsIgnoreCase("null") ? Double.parseDouble(record.get(3)) : null;
				hv.setValue(value);
				hv.setDate(DateUtil.toDate(record.get(5), "yyyy/MM/dd hh:mm:ss aaa"));
				
				switch(record.get(2)){
				case "openPrice":
					openPrice.add(hv);
					break;
				case "closePrice":
					price.add(hv);
					break;
				case "lowPrice":
					lowPrice.add(hv);
					break;
				case "highPrice":
					highPrice.add(hv);
					break;
				case "volume":
					volume.add(hv);
					break;
				}
			}
		}
		ph.setHighPrice(highPrice);
		ph.setLowPrice(lowPrice);
		ph.setOpenPrice(openPrice);
		ph.setPrice(price);
		ph.setVolume(volume);
		
		return ph;
	}
	
}
