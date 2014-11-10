package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

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
	
	private ClassPathResource template1 = new ClassPathResource("META-INF/query/capiq/priceHistory.json");
	private ClassPathResource template2 = new ClassPathResource("META-INF/query/capiq/priceHistory2.json");
	private ClassPathResource template3 = new ClassPathResource("META-INF/query/capiq/priceHistory3.json");
	
	@Override
	public PriceHistory load(String id, String... parms) throws CapIQRequestException, ResponseParserException {

		Assert.notEmpty(parms);
		
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", parms[0]);
		ctx.put("endDate", parms[1]);

		CapIQResponse response1 = requestExecutor.execute(new CapIQRequestImpl(template1), ctx);
		CapIQResponse response2 = requestExecutor.execute(new CapIQRequestImpl(template2), ctx);
		CapIQResponse response3 = requestExecutor.execute(new CapIQRequestImpl(template3), ctx);

		CapIQResult prices = response1.getResults().get(0);
		CapIQResult volumes = response1.getResults().get(1);
		CapIQResult highprices = response2.getResults().get(0);
		CapIQResult lowprices = response2.getResults().get(1);
		CapIQResult openprices = response3.getResults().get(0);
		
		
		// Strip exchange extension from ticker if present
		int exIndex = id.indexOf(':');
		
		if(exIndex >= 0)
			id = id.substring(0, exIndex );
			
		List<HistoricalValue> price = getHistory(prices, id);
		List<HistoricalValue> highPrice = getHistory(highprices, id);
		List<HistoricalValue> lowPrice = getHistory(lowprices, id);
		List<HistoricalValue> openPrice = getHistory(openprices, id);
		List<HistoricalValue> volume = getHistory(volumes, id);

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

}
