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
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;

public class CompanyService extends AbstractDataService{

	@Autowired
	private HistoricalService historicalService;

	private CapIQRequest companyRequest() {
		return new CapIQRequest(new ClassPathResource("META-INF/query/capiq/companyInfo.json"));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Company load(String id, String... parms) throws ResponseParserException, CapIQRequestException{

		Assert.notEmpty(parms);
		
		String startDate = parms[0];
		Company company = executeRequest(id, startDate);

		loadPreviousClose(company);
		loadHistorical(company, startDate);

		return company;
	}

	private Company loadPreviousClose(Company comp) throws ResponseParserException, CapIQRequestException {

		Date d = comp.getPreviousCloseDate();

		if(d != null){
			Company previousDay = executeRequest(comp.getTickerCode(), DateUtil.fromDate(d));

			if(previousDay != null)
				comp.setPreviousClosePrice(previousDay.getClosePrice());
		}

		return comp;
	}
	
	private Company loadHistorical(Company comp, final String startDate) throws ResponseParserException, CapIQRequestException {

		String yearAgo = DateUtil.adjustDate(startDate, Calendar.YEAR, -1);
		List<List<HistoricalValue>> historicalData = historicalService.load(comp.getTickerCode(), yearAgo);

		List<HistoricalValue> lastYearPrice = historicalData.get(0);
		List<HistoricalValue> lastYearVolume = historicalData.get(1);

		comp.setAvgVolumeM3(getThreeMonthAvg(lastYearVolume, startDate));
		comp.setPriceHistory(lastYearPrice);

		return comp;
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
