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
	
	/*@Value ("${loader.dir.cache}")
	private String cachePath;	
	private ClassPathResource template = new ClassPathResource("META-INF/query/capiq/priceHistory.json");
	private ClassPathResource template2 = new ClassPathResource("META-INF/query/capiq/priceHistory2.json");
	
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
		String fileStart = "pricehistory_"+id;
		List<String>listOfFiles = requestExecutor.loadFiles(fileStart, cachePath);
		//Checks if cached file for yesterday exists
		String startDate = getTime(parms[0]).trim();
		String yearAgo = getTime(DateUtil.adjustDate(parms[1], Calendar.YEAR, -1)).trim();
		String yesterday = getTime(DateUtil.adjustDate(parms[1], Calendar.DAY_OF_MONTH, -1));
		for(String file : listOfFiles){
			if (file.startsWith("pricehistory_"+id+"_"+yesterday) && !startDate.equals(yearAgo)){
				//Sets start point to today
				ctx.put("startDate", parms[1]);
			}
		}
		CapIQResponse response;
		if(startDate.equals(yearAgo)){
			response = requestExecutor.execute(new CapIQRequestImpl(template2), ctx);
		}else{
			response = requestExecutor.execute(new CapIQRequestImpl(template), ctx);
		}
		//Writes todays file
		String dateString = getTime(parms[1]);
		String path2 = cachePath + "pricehistory_" + id + "_" + dateString + ".json";
		//Error Response Check
		if(response.getErrorMsg() == null){
			requestExecutor.writeFile(path2, response);
		}		
		//Gets all files associated with ticket code
		listOfFiles = requestExecutor.loadFiles(fileStart, cachePath);
		List<String> matches = new ArrayList<String>();
		for(String file: listOfFiles){
			if (file.startsWith("pricehistory_"+id) && !startDate.equals(yearAgo)){
				matches.add(file);
			}else if(file.startsWith("pricehistory_" + id + "_" + getTime(parms[1])+".json") && startDate.equals(yearAgo)){
				matches.add(file);
			}
		}
		//Orders list by name
		Collections.sort(matches);		
		List<HistoricalValue> price = new ArrayList<HistoricalValue>();
		List<HistoricalValue> volume = new ArrayList<HistoricalValue>();
		List<HistoricalValue> highPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> lowPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> openPrice = new ArrayList<HistoricalValue>();						    
		//Reads File
		CapIQResponse resp = null;
		for(String currentFile: matches){
			String path = cachePath + currentFile;
			resp = requestExecutor.readFile(path);
			//Reads file data
			CapIQResult prices = resp.getResults().get(0);
			CapIQResult volumes = resp.getResults().get(1);				
			//Adds file data to current list
			price.addAll(getHistory(prices, id));
			volume.addAll(getHistory(volumes, id));				
			if(!startDate.equals(yearAgo) && resp.getResults().size() > 2){
				CapIQResult highprices = resp.getResults().get(2);
				CapIQResult lowprices = resp.getResults().get(3);
				CapIQResult openprices = resp.getResults().get(4);
				highPrice.addAll(getHistory(highprices, id));
				lowPrice.addAll(getHistory(lowprices, id));
				openPrice.addAll(getHistory(openprices, id));
			}
		}
		//Loads data lists into PriceHistory
		PriceHistory history = new PriceHistory();
		history.setPrice(price);
		history.setHighPrice(highPrice);
		history.setLowPrice(lowPrice);
		history.setOpenPrice(openPrice);
		history.setVolume(volume);		
		if(history.getPrice().size() != history.getHighPrice().size() || history.getPrice().size() != history.getLowPrice().size() || history.getPrice().size() != history.getOpenPrice().size()){
			HistoryProcess process = new HistoryProcess();
			history = process.processHistory(history);
		}		
		return history;
	}*/
	
	@Override
	public PriceHistory load(String id, String... parms) throws CapIQRequestException, ResponseParserException {
		
		Assert.notEmpty(parms);		
		id = id.split(":")[0];
		
		return getHistoricalData(id);
		
	}
	
	public PriceHistory getHistoricalData(String id) throws ResponseParserException, CapIQRequestException {			
		PriceHistory ph = new PriceHistory();
		String file = "src/main/resources/data/company-data.csv";
		CSVHelperUtil csvHelper = new CSVHelperUtil();
		Iterable<CSVRecord> records = csvHelper.getRecords(file);		
		List<String> list = Arrays.asList("openPrice", "closePrice", "volume", "highPrice", "lowPrice");
		
		List<HistoricalValue> price = new ArrayList<HistoricalValue>();
		List<HistoricalValue> highPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> lowPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> openPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> volume = new ArrayList<HistoricalValue>();
		
		for (CSVRecord record : records) {
			if(record.get(0).equalsIgnoreCase(id) && list.contains(record.get(2))){
				HistoricalValue hv = new HistoricalValue();
				hv.setTickerCode(id);								
				
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
