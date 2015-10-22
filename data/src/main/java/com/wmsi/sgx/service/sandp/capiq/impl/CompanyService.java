package com.wmsi.sgx.service.sandp.capiq.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;

@SuppressWarnings("unchecked")
public class CompanyService extends AbstractDataService {
	
	private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

	private PriceHistory priceHistory;

	@Override
	public Company load(String id, String... parms) throws ResponseParserException, CapIQRequestException {

		Assert.notEmpty(parms);

		String startDate = parms[0];
		Company company = getCompany(id);
		
		priceHistory = getPreviousClose(id);
		Collections.sort(priceHistory.getPrice(), HistoricalValue.HistoricalValueComparator);
		
		if (priceHistory.getPrice().size() > 0) {
			
			if (priceHistory.getPrice().get(1).getValue() != null) {
				company.setPreviousClosePrice(priceHistory.getPrice().get(1).getValue());
				company.setPreviousCloseDate(priceHistory.getPrice().get(1).getDate());
			}
			
			loadHistorical(priceHistory, startDate, company);
			
		}
		else {
			log.error("NO PRICE HISTORY: " + id);
		}

		return company;
	}

	

	private Company loadHistorical(PriceHistory historicalData, final String startDate, Company comp)
			throws ResponseParserException, CapIQRequestException {
		
		List<HistoricalValue> lastYearPrice = historicalData.getPrice();
		List<HistoricalValue> lastYearVolume = historicalData.getVolume();

		comp.setAvgVolumeM3(getThreeMonthAvg(lastYearVolume, startDate));
		comp.setPriceHistory(lastYearPrice);
		comp.setAvgTradedVolM3(getAvgTradedValueM3(lastYearVolume, lastYearPrice, startDate));

		return comp;
	}

	public Company getCompany(String id) throws ResponseParserException, CapIQRequestException {
		Iterable<CSVRecord> records = getCompanyData(id, "company-data");
		Map<String, Object> map = new HashMap<String, Object>();

		for (CSVRecord record : records) {
			String lastName = record.get(2);
			map.put(lastName, record.get(3));
		}

		if (map.size() == 0) throw new ResponseParserException("record map is empty in getCompany()");

		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
		JsonElement jsonElement = gson.toJsonTree(map);
		Company comp = gson.fromJson(jsonElement, Company.class);
		comp.setTickerCode(id.split(":")[0]);
		return comp;
	}

	public PriceHistory getPreviousClose(String input) throws ResponseParserException, CapIQRequestException {
		
		PriceHistory ph = new PriceHistory();
		Iterable<CSVRecord> records = getCompanyData(input, "company-data");
		List<String> list = Arrays.asList("openPrice", "closePrice", "volume", "highPrice", "lowPrice");
		
		List<HistoricalValue> price = new ArrayList<HistoricalValue>();
		List<HistoricalValue> highPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> lowPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> openPrice = new ArrayList<HistoricalValue>();
		List<HistoricalValue> volume = new ArrayList<HistoricalValue>();
		
		for (CSVRecord record : records) {
			if(list.contains(record.get(2))){
				HistoricalValue hv = new HistoricalValue();
				hv.setTickerCode(input.split(":")[0]);								
				
				Double value  = !record.get(3).equalsIgnoreCase("null") || record.get(3).length() == 0 ? Double.parseDouble(record.get(3)) : null;
				hv.setValue(value);
				hv.setDate(new Date(record.get(5)));
				
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

	private Double getAvgTradedValueM3(List<HistoricalValue> lastYearVol, List<HistoricalValue> lastYearPrice,
			String startDate) {
		List<HistoricalValue> volume = getLastThreeMonths(lastYearVol, startDate);
		List<HistoricalValue> price = getLastThreeMonths(lastYearPrice, startDate);
		List<HistoricalValue> price2 = getLastThreeMonths(lastYearPrice, startDate);

		// Checks for matching price volume size/dates and removes unmatching
		// entries.
		if (volume.size() != price.size()) {
			for (HistoricalValue currPrice : price) {
				Boolean isFound = false;
				for (HistoricalValue currVol : volume) {
					if (currPrice.getDate() == currVol.getDate()) {
						isFound = true;
						break;
					}
				}   
				if (isFound == false) {
					price2.remove(currPrice);
				}
			}
		}

		Double sum = 0.0;

		for (int i = 0; i < price2.size(); i++) {
			HistoricalValue vol = volume.get(i);
			HistoricalValue pri = price2.get(i);
			sum += vol.getValue() * pri.getValue();
		}

		if (sum == 0)
			return 0.0;

		return avg(sum, volume.size(), 4);

	}

	private Double getThreeMonthAvg(List<HistoricalValue> lastYear, String startDate) throws ResponseParserException {

		List<HistoricalValue> volume = getLastThreeMonths(lastYear, startDate);

		Double sum = 0.0;

		for (HistoricalValue v : volume)
			sum += v.getValue();

		if (sum == 0)
			return 0.0;

		return avg(sum, volume.size(), 4);
	}

	private List<HistoricalValue> getLastThreeMonths(List<HistoricalValue> lastYear, String startDate) {

		String threeMonthsAgo = DateUtil.adjustDate(startDate, Calendar.DAY_OF_MONTH, -91);

		List<HistoricalValue> volume = new ArrayList<HistoricalValue>();

		for (HistoricalValue val : lastYear) {
			if (val.getDate().compareTo(DateUtil.toDate(threeMonthsAgo)) >= 0)
				volume.add(val);
		}

		return volume;
	}

	private Double avg(Double sum, Integer total, int scale) {
		BigDecimal s = new BigDecimal(sum);
		BigDecimal t = new BigDecimal(total);
		BigDecimal avg = s.divide(t, RoundingMode.HALF_UP);
		return avg.setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

}
