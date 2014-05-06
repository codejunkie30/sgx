package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestExecutor;
import com.wmsi.sgx.util.DateUtil;

@Service
public class HistoricalService{
	
	private CapIQRequestExecutor requestExecutor;

	public void setRequestExecutor(CapIQRequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	public List<List<HistoricalValue>> getHistoricalData(String id, String asOfDate) throws CapIQRequestException {
		String sDate = DateUtil.adjustDate(asOfDate, Calendar.YEAR, -5);
		return loadHistoricaData(id, sDate);
	}

	private CapIQRequest priceRequest(){
		return new CapIQRequest(new ClassPathResource("META-INF/query/capiq/priceHistory.json"));		
	}

	public List<List<HistoricalValue>> loadHistoricaData(String id, String startDate) throws CapIQRequestException {

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", startDate);

		CapIQResponse response = requestExecutor.execute(priceRequest(), ctx);

		CapIQResult prices = response.getResults().get(0);
		CapIQResult volumes = response.getResults().get(1);
		List<HistoricalValue> price = getHistory(prices, id);
		List<HistoricalValue> volume = getHistory(volumes, id);

		List<List<HistoricalValue>> ret = new ArrayList<List<HistoricalValue>>();
		ret.add(price);
		ret.add(volume);
		return ret;
	}

	private List<HistoricalValue> getHistory(CapIQResult res, String id) {

		List<HistoricalValue> results = new ArrayList<HistoricalValue>();

		// TODO sanity check headers for index of Date
		for(CapIQRow row : res.getRows()){
			List<String> values = row.getValues();

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
