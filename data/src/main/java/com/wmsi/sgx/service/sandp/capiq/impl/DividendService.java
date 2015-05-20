package com.wmsi.sgx.service.sandp.capiq.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.DividendDate;
import com.wmsi.sgx.model.DividendPrice;
import com.wmsi.sgx.model.DividendType;
import com.wmsi.sgx.model.DividendValue;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;

public class DividendService extends AbstractDataService{
	
	private ClassPathResource template = new ClassPathResource("META-INF/query/capiq/dividendHistory.json");

	@SuppressWarnings("unchecked")
	public DividendHistory load(String id, String... parms)
			throws ResponseParserException, CapIQRequestException {
		Assert.notEmpty(parms);		
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", parms[0]);
		ctx.put("endDate", parms[1]);
		// Strip exchange extension from ticker if present
		int exIndex = id.indexOf(':');
		if(exIndex >= 0)
			id = id.substring(0, exIndex );	
		CapIQResponse response = requestExecutor.execute(new CapIQRequestImpl(template), ctx);
		
		List<DividendDate> exDate = new ArrayList<DividendDate>();
		List<DividendDate> payDate = new ArrayList<DividendDate>();
		List<DividendPrice> price = new ArrayList<DividendPrice>();
		List<DividendType> type = new ArrayList<DividendType>();
		
		CapIQResult exDates = response.getResults().get(0);
		CapIQResult payDates = response.getResults().get(1);
		CapIQResult prices = response.getResults().get(2);
		CapIQResult types = response.getResults().get(3);
		
		exDate.addAll(getDate(exDates, id));
		payDate.addAll(getDate(payDates, id));
		price.addAll(getPrice(prices, id));
		type.addAll(getType(types, id));
		
		exDate = dateDuplicate(exDate);
		payDate = payDuplicates(payDate, exDate);
		price = priceDuplicates(price, exDate);
		type = typeDuplicates(type, exDate);
		
		
		DividendHistory history = new DividendHistory();
		history.setTickerCode(id);
		List<DividendValue> ret = new ArrayList<DividendValue>();
		
		for(int i = 0; i < exDate.size(); i++){
			DividendValue value = new DividendValue();
			value.setDividendExDate(exDate.get(i).getDateValue());
			value.setDividendPayDate(payDate.get(i).getDateValue());
			value.setDividendPrice(price.get(i).getPrice());
			value.setDividendType(type.get(i).getDivType());
			ret.add(value);
			
		}
		history.setDividendValues(ret);
		
		return history;
	}
	
	private List<DividendDate> dateDuplicate(List<DividendDate> dates){
		List<DividendDate> ret = new ArrayList<DividendDate>();
		ret.add(dates.get(0));
		for(int i = 1; i < dates.size(); i++){
			if(!dates.get(i).getDateValue().equals(dates.get(i-1).getDateValue()))
				ret.add(dates.get(i));
		}
		if(ret.size() > 10)
			ret = ret.subList(ret.size()-10, ret.size());
		return ret;
	}
	
	private List<DividendDate> payDuplicates(List<DividendDate> dates, List<DividendDate> sortedDates){
		List<DividendDate> ret = new ArrayList<DividendDate>();
		for(int i = 0; i < dates.size(); i++){
			for(DividendDate data : sortedDates){
				if(dates.get(i).getAsOfDate().equals(data.getAsOfDate()))
					ret.add(dates.get(i));
			}
		}
		return ret;
	}
	
	private List<DividendPrice> priceDuplicates(List<DividendPrice> prices, List<DividendDate> sortedDates){
		List<DividendPrice> ret = new ArrayList<DividendPrice>();
		for(int i = 0; i < prices.size(); i++){
			for(DividendDate data : sortedDates){
				if(prices.get(i).getAsOfDate().equals(data.getAsOfDate()))
					ret.add(prices.get(i));
			}
		}
		return ret;
	}
	
	private List<DividendType> typeDuplicates(List<DividendType> types, List<DividendDate> sortedDates){
		List<DividendType> ret = new ArrayList<DividendType>();
		for(int i = 0; i < types.size(); i++){
			for(DividendDate data : sortedDates){
				if(types.get(i).getAsOfDate().equals(data.getAsOfDate()))
					ret.add(types.get(i));
			}
		}
		return ret;
	}
	private List<DividendDate> getDate(CapIQResult res, String id) throws CapIQRequestException{
		List<DividendDate> results = new ArrayList<DividendDate>();
		for(CapIQRow row : res.getRows()){
			List<String> values = row.getValues();
			if(values.size() < 2)
				throw new CapIQRequestException("Dividend data response is missing fields");
			DividendDate val = new DividendDate();
			val.setTickerCode(id);
			val.setDateValue(DateUtil.toDate(getTime(values.get(0))));
			val.setAsOfDate(DateUtil.toDate(values.get(1)));
			results.add(val);
		}
		return results;
	}
	
	private List<DividendPrice> getPrice(CapIQResult res, String id) throws CapIQRequestException{
		List<DividendPrice> results = new ArrayList<DividendPrice>();
		for(CapIQRow row : res.getRows()){
			List<String> values = row.getValues();
			if(values.size() < 2)
				throw new CapIQRequestException("Dividend data response is missing fields");
			DividendPrice val = new DividendPrice();
			val.setTickerCode(id);
			val.setPrice(Double.valueOf(values.get(0)));
			val.setAsOfDate(DateUtil.toDate(values.get(1)));
			results.add(val);
		}
		return results;
	}
	
	private List<DividendType> getType(CapIQResult res, String id) throws CapIQRequestException{
		List<DividendType> results = new ArrayList<DividendType>();
		for(CapIQRow row : res.getRows()){
			List<String> values = row.getValues();
			if(values.size() < 2)
				throw new CapIQRequestException("Dividend data response is missing fields");
			DividendType val = new DividendType();
			val.setTickerCode(id);
			val.setDivType(values.get(0));
			val.setAsOfDate(DateUtil.toDate(values.get(1)));
			results.add(val);
		}
		return results;
	}
	
	
	
	private String getTime(String object) throws CapIQRequestException{
		Date date;
		try {
			date = new SimpleDateFormat("MMM dd yyyy hh:mma").parse(object);
			return new SimpleDateFormat("MM/dd/yyyy").format(date);
		} catch (ParseException e) {
			throw new CapIQRequestException("Invalid Date format or data.", e);
		}
	}
	
	
}