package com.wmsi.sgx.service.sandp.capiq.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.DataService;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;

@SuppressWarnings("unchecked")
public class CompanyService extends AbstractDataService{

	@Autowired
	private DataService historicalService;

	private CapIQRequestImpl companyRequest() {
		return new CapIQRequestImpl(new ClassPathResource("META-INF/query/capiq/companyInfo.json"));
	}
	
	@Override	
	public Company load(String id, String... parms) throws ResponseParserException, CapIQRequestException{

		Assert.notEmpty(parms);
		
		String startDate = parms[0];
		Company company = executeRequest(id, startDate);

		loadPreviousClose(company, id);
		loadHistorical(company, id, startDate);

		return company;
	}

	private Company loadPreviousClose(Company comp, String id) throws ResponseParserException, CapIQRequestException {

		Date d = comp.getPreviousCloseDate();

		if(d != null){
			Company previousDay = executeRequest(id, DateUtil.fromDate(d));

			if(previousDay != null)
				comp.setPreviousClosePrice(previousDay.getClosePrice());
		}

		return comp;
	}
	
	private Company loadHistorical(Company comp, final String id, final String startDate) throws ResponseParserException, CapIQRequestException {

		String yearAgo = DateUtil.adjustDate(startDate, Calendar.YEAR, -1);
		PriceHistory historicalData = historicalService.load(id, yearAgo, startDate);

		List<HistoricalValue> lastYearPrice = historicalData.getPrice();
		List<HistoricalValue> lastYearVolume = historicalData.getVolume();

		comp.setAvgVolumeM3(getThreeMonthAvg(lastYearVolume, startDate));
		comp.setPriceHistory(lastYearPrice);
		comp.setAvgTradedVolM3(getAvgTradedValueM3(lastYearVolume,lastYearPrice, startDate));

		return comp;
	}
	
	private Double getAvgTradedValueM3(List<HistoricalValue> lastYearVol, List<HistoricalValue> lastYearPrice, String startDate){
		List<HistoricalValue> volume = getLastThreeMonths(lastYearVol, startDate);
		List<HistoricalValue> price = getLastThreeMonths(lastYearPrice, startDate);
		
		Double sum = 0.0;
		
		for(int i = 0; i < price.size(); i++){
			HistoricalValue vol = volume.get(i);
			HistoricalValue pri = price.get(i);			
			sum += vol.getValue() * pri.getValue();
		}
		
		if(sum == 0)
			return 0.0;
		
		return avg(sum, volume.size(), 4);
		
	}

	private Double getThreeMonthAvg(List<HistoricalValue> lastYear, String startDate) throws ResponseParserException {

		List<HistoricalValue> volume = getLastThreeMonths(lastYear, startDate);

		Double sum = 0.0;

		for(HistoricalValue v : volume)
			sum += v.getValue();

		if(sum == 0)
			return 0.0;

		return avg(sum, volume.size(), 4);
	}

	private List<HistoricalValue> getLastThreeMonths(List<HistoricalValue> lastYear, String startDate) {

		String threeMonthsAgo = DateUtil.adjustDate(startDate, Calendar.DAY_OF_MONTH, -91);

		List<HistoricalValue> volume = new ArrayList<HistoricalValue>();

		for(HistoricalValue val : lastYear){
			if(val.getDate().compareTo(DateUtil.toDate(threeMonthsAgo)) >= 0)
				volume.add(val);
		}

		return volume;
	}

	private Map<String, Object> buildContext(String id, String startDate) {
		String previousDate = DateUtil.adjustDate(startDate, Calendar.DAY_OF_MONTH, -1);

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", startDate);
		ctx.put("previousDate", previousDate);
		return ctx;
	}

	private Company executeRequest(String id, String startDate) throws ResponseParserException, CapIQRequestException {
		Map<String, Object> ctx = buildContext(id, startDate);
		return executeRequest(companyRequest(), ctx);
	}

	private Double avg(Double sum, Integer total, int scale) {
		BigDecimal s = new BigDecimal(sum);
		BigDecimal t = new BigDecimal(total);
		BigDecimal avg = s.divide(t, RoundingMode.HALF_UP);
		return avg.setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

}
