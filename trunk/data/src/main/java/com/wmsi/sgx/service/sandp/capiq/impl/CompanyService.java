package com.wmsi.sgx.service.sandp.capiq.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.FXRecord;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.annotation.FXAnnotation;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CompanyCSVRecord;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;

@SuppressWarnings("unchecked")
public class CompanyService extends AbstractDataService {
	
	private static final Logger log = LoggerFactory.getLogger(CompanyService.class);
	
	@Override
	public Company load(String id, String... parms) throws ResponseParserException, CapIQRequestException {

		Assert.notEmpty(parms);
		
		try {
		
			// list of records
			List<CompanyCSVRecord> records = getParsedCompanyRecords(id, "company-data");
			
			// initialize FX data
			List<FXRecord> fxRecords = initFXRecords(id, records);
			
			Company company = getCompany(id, records, fxRecords);
			
			PriceHistory priceHistory = loadPriceHistory(id, records, fxRecords);
			company.fullPH = priceHistory;
	
			Collections.sort(priceHistory.getPrice(), HistoricalValue.HistoricalValueComparator);
			
			if (priceHistory.getPrice().size() > 0) {
				
				company.setClosePrice(priceHistory.getPrice().get(0).getValue());
				
				if (priceHistory.getPrice().size() > 1 && priceHistory.getPrice().get(1).getValue() != null) {
					company.setPreviousClosePrice(priceHistory.getPrice().get(1).getValue());
					company.setPreviousCloseDate(priceHistory.getPrice().get(1).getDate());
				}
				
				loadHistorical(parms[0], company);
				
			}
			else {
				log.error("NO PRICE HISTORY: " + id);
			}
			
			return company;
			
		}
		catch(Exception e) {
			throw new ResponseParserException("Loading company: " + id, e);
		}
		
	}

	/**
	 * load any of the fx records needed for this record
	 * @param records
	 * @throws ResponseParserException
	 */
	public List<FXRecord> initFXRecords(String id, List<CompanyCSVRecord> records) throws ResponseParserException  {
		List<String> currenciesList = new ArrayList<String>();
		for (CompanyCSVRecord record : records) {
			if (record.getCurrency() == null || currenciesList.contains(record.getCurrency())) continue;
			currenciesList.add(record.getCurrency());
		}
		try { return getFXData(id, currenciesList); }
		catch(Exception e) { throw new ResponseParserException("Loading FX data", e); }
	}

	/**
	 * load up the base fields
	 * @param id
	 * @param records
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public Company getCompany(String id, List<CompanyCSVRecord> records, List<FXRecord> fxRecords) throws ResponseParserException, CapIQRequestException {
		
		
		Field[] fields = Company.class.getDeclaredFields();
		Map<String, Object> map = new HashMap<String, Object>();

		for (Field field : fields) {
			String name = field.getName();
			String val = getFieldValue(field, records, fxRecords);
			if (val == null) continue;
			map.put(name, val);
		}
		
		if (map.size() == 0) throw new ResponseParserException("record map is empty in getCompany()");

		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
		JsonElement jsonElement = gson.toJsonTree(map);
		Company comp = gson.fromJson(jsonElement, Company.class);
		comp.setTickerCode(id.split(":")[0]);
		comp.setExchange(id.split(":")[1]);
		return comp;
	}
	
	/**
	 * get the value for a particular field
	 * @param name
	 * @param records
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public String getFieldValue(Field field, List<CompanyCSVRecord> records, List<FXRecord> fxRecords) throws ResponseParserException, CapIQRequestException {
		
		CompanyCSVRecord actual = null;
		
		for (CompanyCSVRecord record : records) {
			if (!record.getName().equals(field.getName())) continue;

			// becomes actual if most recent or no date specified
			if (actual == null || record.getPeriodDate() == null || record.getPeriodDate().after(actual.getPeriodDate())) actual = record;
			
		}
		
		return actual == null ? null : getFXConverted(field, actual, fxRecords);
	}
	
	/**
	 * handle FX conversion if necessary
	 * @param field
	 * @param actual
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public String getFXConverted(Field field, CompanyCSVRecord actual, List<FXRecord> fxRecords) throws ResponseParserException, CapIQRequestException {
		if (!field.isAnnotationPresent(FXAnnotation.class)) return actual.getValue();
		return getFXConverted(actual, fxRecords);
	}
	
	/**
	 * convert the value of csv record (assumes it can be converted)
	 * @param actual
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public String getFXConverted(CompanyCSVRecord actual, List<FXRecord> fxRecords) throws ResponseParserException, CapIQRequestException {
		
		// ignore for now
		if (fxRecords.size() == 0 || actual == null || actual.getCurrency() == null || actual.getCurrency().equals("SGD")) return actual.getValue();

		for (FXRecord record : fxRecords) {
			if (!record.getFrom().equals(actual.getCurrency()) || !DateUtils.isSameDay(actual.getPeriodDate(), record.getDate())) continue;
			BigDecimal val = BigDecimal.valueOf(record.getMultiplier()).multiply(new BigDecimal(actual.getValue()));
			return val.toString();
		}
		
		throw new ResponseParserException("No conversion available for field " + actual +  "\n" + fxRecords.toString());
		
		
	}
	
	/**
	 * load up the price history	
	 * @param input
	 * @param records
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public PriceHistory loadPriceHistory(String input, List<CompanyCSVRecord> records, List<FXRecord> fxRecords) throws ResponseParserException, CapIQRequestException {
		
		PriceHistory ph = new PriceHistory();
		Map<String, List<HistoricalValue>> pricing = new HashMap<String, List<HistoricalValue>>();
		pricing.put("openPrice", new ArrayList<HistoricalValue>());
		pricing.put("closePrice", new ArrayList<HistoricalValue>());
		pricing.put("volume", new ArrayList<HistoricalValue>());
		pricing.put("highPrice", new ArrayList<HistoricalValue>());
		pricing.put("lowPrice", new ArrayList<HistoricalValue>());
		
		
		for (CompanyCSVRecord record : records) {
			
			List<HistoricalValue> list = pricing.get(record.getName());
			if (list == null || record == null) continue;
			
			try {
				HistoricalValue hv = new HistoricalValue();
				String val = StringUtils.stripToNull(getFXConverted(record, fxRecords));
				hv.setTickerCode(record.getTicker());
				hv.setDate(record.getPeriodDate());
				if (val != null) hv.setValue(Double.parseDouble(val));
				list.add(hv);
			}
			catch(Exception e) {
				log.error("Trying to load hv record: " + record.toString(), e);
			}
			
		}
		
		
		ph.setHighPrice(pricing.get("highPrice"));
		ph.setLowPrice(pricing.get("lowPrice"));
		ph.setOpenPrice(pricing.get("openPrice"));
		ph.setPrice(pricing.get("closePrice"));
		ph.setVolume(pricing.get("volume"));
		
		return ph;
	}

	private Company loadHistorical(final String startDate, Company comp) throws ResponseParserException, CapIQRequestException {
		
		PriceHistory historicalData = comp.fullPH;
		List<HistoricalValue> lastYearPrice = historicalData.getPrice();
		List<HistoricalValue> lastYearVolume = historicalData.getVolume();

		comp.setAvgVolumeM3(getThreeMonthAvg(lastYearVolume, startDate));
		comp.setPriceHistory(lastYearPrice);
		comp.setAvgTradedVolM3(getAvgTradedValueM3(lastYearVolume, lastYearPrice, startDate));

		return comp;
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

		for (HistoricalValue v : volume) {
			if (v == null || v.getValue() == null) continue;
			sum += v.getValue();
		}
			
		if (sum == 0) return 0.0;

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
