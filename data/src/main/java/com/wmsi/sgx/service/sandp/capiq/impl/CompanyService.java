package com.wmsi.sgx.service.sandp.capiq.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorIndexerService;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CompanyCSVRecord;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;

@SuppressWarnings("unchecked")
public class CompanyService extends AbstractDataService {

	private static final Logger log = LoggerFactory.getLogger(CompanyService.class);
	
	@Autowired
	private AlphaFactorIndexerService alphaFactorService;
	
	@Value("${loader.company-data.dir}")
	private String companyDataDir;
	
	@Value("${loader.consensus-estimates.dir}")
	private String consensusEstimatesDir;
	
	/**
	 * Load Company data from company ticker
	 * @param ticker code 
	 * @return Company object
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override
	public Company load(String id, String... parms) throws ResponseParserException, CapIQRequestException {

		Assert.notEmpty(parms);

		try {

			// list of records
			List<CompanyCSVRecord> records = getParsedCompanyRecords(id, companyDataDir);
			setConsensus(id, records);
			// company
			Company company = getCompany(id, records);
			
			// price history
			PriceHistory priceHistory = loadPriceHistory(id, records);
			company.fullPH = priceHistory;
			// misc
			if (priceHistory.getPrice().size() > 0) {
				Collections.sort(priceHistory.getPrice(), HistoricalValue.HistoricalValueComparator);

				// not sure what the difference is, but the app they should be same
				company.setClosePrice(priceHistory.getPrice().get(0).getValue());
				company.setPreviousClosePrice(priceHistory.getPrice().get(0).getValue());
				company.setPreviousCloseDate(priceHistory.getPrice().get(0).getDate());

				loadHistorical(parms[0], company);

			} else {
				log.error("NO PRICE HISTORY: " + id);
			}

			processMillionRangeValues(company);
			
			setMisc(company, records);
			
			// SGX not allowed to show ASEAN companies in alpha factor or Industry search
			if(!company.getExchange().equals("SGX") && !company.getExchange().equals("CATALIST")){
				company.setIndustry(null);
				company.setIndustryGroup(null);
				company.setGvKey(null);
			}
			
			return company;

		} catch (Exception e) {
			throw new ResponseParserException("Loading company: " + id, e);
		}

	}
	
	/**
	 * add some consensus records straight from the csv file
	 */
	public void setConsensus(String id, List<CompanyCSVRecord> records) throws Exception {
		
		String[] fieldNames = { "avgBrokerReq", "targetPrice" };
		List<String> fields = Arrays.asList(fieldNames);
		
		try {
			List<CompanyCSVRecord> consensus = getParsedCompanyRecords(id, consensusEstimatesDir);
			for (CompanyCSVRecord item : consensus) {
				if (fields.contains(item.getName())) records.add(item);
			}
		}
		catch(Exception e) {}
		
	}

	/**
	 * set some misc properties
	 */
	public void setMisc(Company comp, List<CompanyCSVRecord> records) throws Exception {

		// bunches of GV keys come back, need to just get the one for AF
		for (CompanyCSVRecord record : records) {
			
			if (record.getName().equals("gvKey")) {
				if (alphaFactorService.isAlphaCompany(record.getValue())) {
					comp.setGvKey(record.getValue());
					break;
				}
			}
			
		}
		
		// get the latest value for an item
		// these have multiple rows, we want the latest date or LTM (LTM preferred)
		String[] fields = { "evEbitData", "totalDebtEbitda", "ebitda", "returnOnEquity", "totalRev1YrAnnGrowth", "netProfitMargin" };
		for (String field : fields) {
			setLatestFieldValue(field, comp, records);
		}

	}
	
	public void setLatestFieldValue(String name, Company company, List<CompanyCSVRecord> records) throws Exception {
		
		Field field = Company.class.getDeclaredField(name);
		field.setAccessible(true);
		
		String value = null;
		
		for (CompanyCSVRecord record : records) {
			if (!record.getName().equals(field.getName())) continue;
			// assume it's the latest
			if (record.getPeriod() == null || record.getPeriod().indexOf("LTM") != -1) {
				value = getFieldValue(field, record);
				break;
			}
		}
		
		Double val = null;
		if (StringUtils.stripToNull(value) != null) val = Double.parseDouble(value);
		field.set(company, val);
		
	}
	

	/**
	 * load up the base fields
	 * 
	 * @param id
	 * @param records
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public Company getCompany(String id, List<CompanyCSVRecord> records)
			throws ResponseParserException, CapIQRequestException {

		Field[] fields = Company.class.getDeclaredFields();
		Map<String, Object> map = new HashMap<String, Object>();

		for (Field field : fields) {
			String name = field.getName();
			try {
				Object val = getFieldValue(field, records);
				if (val == null) val = getFieldDate(field, records);
				if (val != null) map.put(name, val);
			} catch (Exception e) {
				log.error("Getting field val: " + field.getName(), e);
			}
		}
		
		if (map.size() == 0) throw new ResponseParserException("record map is empty in getCompany()");
		
		Gson gson = new GsonBuilder().setDateFormat(DT_FMT_STR).create();
		JsonElement jsonElement = gson.toJsonTree(map);

		Company comp = gson.fromJson(jsonElement, Company.class);
		comp.setTickerCode(id.split(":")[0]);
		comp.setExchange(id.split(":")[1]);
		return comp;
	}

	/**
	 * load up the price history
	 * 
	 * @param input
	 * @param records
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public PriceHistory loadPriceHistory(String input, List<CompanyCSVRecord> records)
			throws ResponseParserException, CapIQRequestException {

		PriceHistory ph = new PriceHistory();
		Map<String, List<HistoricalValue>> pricing = new HashMap<String, List<HistoricalValue>>();
		Field field = null;
		try {
			field = HistoricalValue.class.getDeclaredField("value");
		} catch (Exception e) {
		}

		pricing.put("openPrice", new ArrayList<HistoricalValue>());
		pricing.put("closePrice", new ArrayList<HistoricalValue>());
		pricing.put("volume", new ArrayList<HistoricalValue>());
		pricing.put("highPrice", new ArrayList<HistoricalValue>());
		pricing.put("lowPrice", new ArrayList<HistoricalValue>());

		for (CompanyCSVRecord record : records) {

			List<HistoricalValue> list = pricing.get(record.getName());
			if (list == null || record == null)
				continue;

			try {
				String val = getFieldValue(field, record);
				HistoricalValue hv = new HistoricalValue();
				hv.setTickerCode(record.getTicker());
				hv.setDate(record.getPeriodDate());
				if (val != null) {
					Double dbl = Double.parseDouble(val);
					if (record.getName().equals("volume")) dbl = processMillionFormatter(dbl);
					hv.setValue(dbl);
				}
					
				list.add(hv);
			} catch (Exception e) {
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
	/**
	 * load historical data for a company 
	 * @param start date 
	 * @param company
	 * @return Company Object
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	private Company loadHistorical(final String startDate, Company comp)
			throws ResponseParserException, CapIQRequestException {

		PriceHistory historicalData = comp.fullPH;
		List<HistoricalValue> lastYearPrice = historicalData.getPrice();
		List<HistoricalValue> lastYearVolume = historicalData.getVolume();

		comp.setAvgVolumeM3(getThreeMonthAvg(lastYearVolume, startDate));
		comp.setPriceHistory(lastYearPrice);
		comp.setAvgTradedVolM3(getAvgTradedValueM3(lastYearVolume, lastYearPrice, startDate));

		return comp;
	}
	
	/**
	 * Get Average traded Value 
	 * @param lastYearVol
	 * @param lastYearPrice
	 * @param startDate 
	 * @return List<HistoricalValue>
	 *
	 */
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
			if(vol.getValue()!=null && pri.getValue() !=null){
				sum += vol.getValue() * pri.getValue();
			}
		}

		if (sum == 0)
			return 0.0;

		return avg(sum, volume.size(), 4);

	}
	
	/**
	 * Get Three Month average historical data 
	 * @param start date 
	 * @return average value
	 * @throws ResponseParserException
	 */
	private Double getThreeMonthAvg(List<HistoricalValue> lastYear, String startDate) throws ResponseParserException {

		List<HistoricalValue> volume = getLastThreeMonths(lastYear, startDate);

		Double sum = 0.0;

		for (HistoricalValue v : volume) {
			if (v == null || v.getValue() == null)
				continue;
			sum += v.getValue();
		}

		if (sum == 0)
			return 0.0;

		return avg(sum, volume.size(), 4);
	}
	/**
	 * Get Last Three Months historical data 
	 * @param start date 
	 * @return List<HistoricalValue>
	 *
	 */
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
