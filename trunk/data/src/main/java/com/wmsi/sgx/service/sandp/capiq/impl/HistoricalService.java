package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;

@SuppressWarnings("unchecked")
public class HistoricalService extends AbstractDataService {
	
	private ClassPathResource template = new ClassPathResource("META-INF/query/capiq/priceHistory.json");
	
	@Override
	public PriceHistory load(String id, String... parms) throws CapIQRequestException, ResponseParserException {

		Assert.notEmpty(parms);
		
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", parms[0]);
		ctx.put("endDate", parms[1]);
		
		// Strip exchange extension from ticker if present
		int exIndex = id.indexOf(':');
		
		if(exIndex >= 0)
			id = id.substring(0, exIndex );
		ObjectMapper mapper = new ObjectMapper();
		
		File folder = new File("/mnt/cache/");
		File[] listOfFiles = folder.listFiles();
		List<String> matches = new ArrayList<String>();
		Boolean cached = false;
		String lastUpdated = null;

		//Checks if cached file for yesterday exists
		String yesterday = getTime(DateUtil.adjustDate(parms[1], Calendar.DAY_OF_MONTH, -1));
		String today = getTime(parms[1]);
		for(File file : listOfFiles){
			if (file.getName().startsWith("pricehistory_"+id+"_"+today)){
				lastUpdated = today;
			}
			if (file.getName().startsWith("pricehistory_"+id+"_"+yesterday) && lastUpdated != today){
				lastUpdated = yesterday;
				//Sets start point to today
				ctx.put("startDate", parms[1]);
			}
		}
		if(lastUpdated != today){
			CapIQResponse response = requestExecutor.execute(new CapIQRequestImpl(template), ctx);
			
			//Writes todays file
			String dateString = getTime(parms[1]);
			String path2 = "/mnt/cache/pricehistory_" + id + "_" + dateString + ".json";
			try {
				mapper.writeValue(new File(path2), response);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Gets all files associated with ticket code
		listOfFiles = folder.listFiles();
		for(File file: listOfFiles){
			if (file.getName().startsWith("pricehistory_"+id)){
				matches.add(file.getName());
			}
		}
		//Orders list by name
		Collections.sort(matches);
					
		
		List<HistoricalValue> price = new ArrayList<HistoricalValue>();
		List<HistoricalValue> highPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> lowPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> openPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> volume = new ArrayList<HistoricalValue>();
				    
		//Reads File
		CapIQResponse resp = null;
		for(String currentFile: matches){
			String path = "/mnt/cache/" + currentFile;
			try {
				resp = mapper.readValue(new File(path), CapIQResponse.class);
				
				//Reads file data
				CapIQResult prices = resp.getResults().get(0);
				CapIQResult volumes = resp.getResults().get(1);
				CapIQResult highprices = resp.getResults().get(2);
				CapIQResult lowprices = resp.getResults().get(3);
				CapIQResult openprices = resp.getResults().get(4);
				
				//Adds file data to current list
				price.addAll(getHistory(prices, id));
				highPrice.addAll(getHistory(highprices, id));
				lowPrice.addAll(getHistory(lowprices, id));
				openPrice.addAll(getHistory(openprices, id));
				volume.addAll(getHistory(volumes, id));
				
			} catch (JsonParseException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		//Loads data lists into PriceHistory
		PriceHistory history = new PriceHistory();
		history.setPrice(price);
		history.setHighPrice(highPrice);
		history.setLowPrice(lowPrice);
		history.setOpenPrice(openPrice);
		history.setVolume(volume);
		return history;
	}

	private List<HistoricalValue> getHistory(CapIQResult res, String id) throws CapIQRequestException {

		List<HistoricalValue> results = new ArrayList<HistoricalValue>();

		for(CapIQRow row : res.getRows()){
			List<String> values = row.getValues();

			if(values.size() < 2)
				throw new CapIQRequestException("Historical data response is missing fields");
			
			String v = values.get(0);
			
			if(NumberUtils.isNumber(v)){
				
				HistoricalValue val = new HistoricalValue();
				val.setTickerCode(id);
				val.setValue(Double.valueOf(v));
				val.setDate(DateUtil.toDate(values.get(1)));
				results.add(val);
			}
		}

		return results;
	}
	
	private String getTime(String object){

		Date date;
		try {
			date = new SimpleDateFormat("MM/dd/yyyy").parse(object);
			return new SimpleDateFormat("MM-dd-yyyy").format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		


	}

}
