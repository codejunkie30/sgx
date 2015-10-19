package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.json.JsonObjectMapper;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.DividendValue;
import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holder;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

//@Service
public class XpressFeed implements CapIQService{
	private static final Logger log = LoggerFactory.getLogger(XpressFeed.class);

	@Override
	public PriceHistory getHistoricalData(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException {			
		PriceHistory ph = new PriceHistory();
		String file = "src/main/resources/data/company-data.csv";
		Iterable<CSVRecord> records = getRecords(file);		
		List<String> list = Arrays.asList("openPrice", "closePrice", "volume", "highPrice", "lowPrice");
		
		List<HistoricalValue> price = new ArrayList<HistoricalValue>();
		List<HistoricalValue> highPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> lowPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> openPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> volume = new ArrayList<HistoricalValue>();
		
		for (CSVRecord record : records) {
			if(record.get(0).equalsIgnoreCase(input.getTicker()) && list.contains(record.get(2))){
				HistoricalValue hv = new HistoricalValue();
				hv.setTickerCode(input.getTicker());								
				
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
		
		System.out.println("High Price: " + highPrice.get(0));
		System.out.println("Low Price: " + lowPrice.get(0));
		System.out.println("Open Price: " + openPrice.get(0));
		System.out.println("Price: " + price.get(0));
		System.out.println("Volume: " + volume.get(0));
		return ph;
	}

	@Override
	public Company getCompany(CompanyInputRecord input)	throws ResponseParserException, CapIQRequestException {		
		String file = "src/main/resources/data/companyData_test.csv";
		Iterable<CSVRecord> records = getRecords(file);		
		Map<String, Object> map = new HashMap<String, Object>();
		
		for (CSVRecord record : records) {
			if(record.get(0).equalsIgnoreCase(input.getTicker())){
				String lastName = record.get(2);
		    	map.put(lastName, record.get(3));
			}
		}
		
		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();		
		JsonElement jsonElement = gson.toJsonTree(map);
		System.out.println(jsonElement);
		Company comp = gson.fromJson(jsonElement, Company.class);
		System.out.println(comp);
		return comp;
	}

	@Override
	public Holders getHolderDetails(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException {
		Holders hol = new Holders();
		hol.setTickerCode(input.getTicker());
		
		String file = "src/main/resources/data/ownership_test.csv";
		Iterable<CSVRecord> records = getRecords(file);
		List<Holder> list = new ArrayList<Holder>();
		
		for (CSVRecord record : records) {
			Holder holder = new Holder();
			holder.setName(record.get(2));
			holder.setShares(Long.parseLong((record.get(3).equals(""))?"0":record.get(3)));
			holder.setPercent(Double.parseDouble((record.get(4).equals(""))?"0":record.get(4)));
			
			list.add(holder);
		}
		
		hol.setHolders(list);
		System.out.println(hol);
		return hol;
	}

	@Override
	public KeyDevs getKeyDevelopments(CompanyInputRecord input)	throws ResponseParserException, CapIQRequestException {
		KeyDevs kD = new KeyDevs();
		kD.setTickerCode(input.getTicker());
		
		String file = "src/main/resources/data/key-devs.csv";
		Iterable<CSVRecord> records = getRecords(file);
		List<KeyDev> list = new ArrayList<KeyDev>();
		for (CSVRecord record : records) {
			KeyDev keydev = new KeyDev();
			String fmt = "yyyy/MM/dd hh:mm:ss aaa";
			keydev.setDate(DateUtil.toDate(record.get(3), fmt));
			keydev.setHeadline(record.get(4));
			keydev.setSituation(record.get(5));
			keydev.setType(record.get(6));
			list.add(keydev);
		}
		kD.setKeyDevs(list);
		return kD;
	}

	@Override
	public Financials getCompanyFinancials(CompanyInputRecord input, String currency) throws ResponseParserException, CapIQRequestException {		
		Financials financials = new Financials();
		financials.setFinancials(new ArrayList<Financial>());
		String file = "src/main/resources/data/company-data.csv";
		
		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
		
		Map<String, List<CSVRecord>> dataMap = getMap(input.getTicker(), getRecords(file));		
		System.out.println(dataMap.keySet());
		Iterator<Entry<String, List<CSVRecord>>> i = dataMap.entrySet().iterator();
		
		while(i.hasNext()){
			Entry<String, List<CSVRecord>> entry = i.next();
			Iterable<CSVRecord> records = entry.getValue();
			Map<String, Object> financialMap = new HashMap<String, Object>();
			financialMap.put("absPeriod", entry.getKey());
			financialMap.put("tickerCode", input.getTicker());
			for (CSVRecord record : records) {				
				financialMap.put(record.get(2), record.get(3));				
			}
			JsonElement jsonElement = gson.toJsonTree(financialMap);
			Financial fin = gson.fromJson(jsonElement, Financial.class);
			List<Financial> list = financials.getFinancials();
			list.add(fin);			
			financials.setFinancials(list);
		}
		System.out.println(financials.getFinancials());
		return financials;
	}

	@Override
	public DividendHistory getDividendData(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException {		
		DividendHistory dH = new DividendHistory();
		dH.setTickerCode(input.getTicker());		
		
		String file = "src/main/resources/data/dividend-history.csv";
		
		Iterable<CSVRecord> records = null;
		records = getRecords(file);
		List<DividendValue> list = new ArrayList<DividendValue>();
				
		for (CSVRecord record : records) {
			if(record.get(0).equalsIgnoreCase(input.getTicker())){
				DividendValue dV = new DividendValue();
				
				String fmt = "yyyy/MM/dd hh:mm:ss aaa";
				dV.setDividendExDate(DateUtil.toDate(record.get(2), fmt));
				dV.setDividendPayDate(DateUtil.toDate(record.get(3), fmt));
				dV.setDividendPrice(Double.parseDouble(record.get(4)));
				dV.setDividendType(record.get(5));				
				
		    	list.add(dV);		    	
		    	
			}
		}
		dH.setDividendValues(list);
		System.out.println(dH);
		return dH;
	}	
	
	public Map<String, List<CSVRecord> > getMap(String ticker, Iterable<CSVRecord> records){
		Map<String, List<CSVRecord>> map = new HashMap<String, List<CSVRecord>>();
		for (CSVRecord record : records) {
			if(record.get(0).equalsIgnoreCase(ticker) && !record.get(4).equalsIgnoreCase("null")){
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
	
	public Iterable<CSVRecord> getRecords(String file){
		Reader in = null;
		try {
			in = new FileReader(file);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException XpressFeed: ", e);
		}
		
		Iterable<CSVRecord> records = null;
		try {
			records = CSVFormat.DEFAULT.parse(in);
		}	catch (IOException e) {
			log.error("IOException XpressFeed: ", e);
		}
		return records;
		
	}
	
	public static void main(String[] args) throws ResponseParserException, CapIQRequestException{
		XpressFeed t = new XpressFeed();
		CompanyInputRecord r = new CompanyInputRecord();
		r.setTicker("C07");
		t.getDividendData(r);
		//t.getCompany(r);
		//t.getHistoricalData(r);
		//t.getCompanyFinancials(r, "SGD");
		//t.getKeyDevelopments(r);
		//t.getHolderDetails(r);
	}

	@Override
	public Estimates getEstimates(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException {
		// TODO Auto-generated method stub
		return null;
	}	
}