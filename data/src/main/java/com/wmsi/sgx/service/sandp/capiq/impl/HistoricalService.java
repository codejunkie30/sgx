package com.wmsi.sgx.service.sandp.capiq.impl;

import org.springframework.util.Assert;

import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class HistoricalService extends AbstractDataService {
	
	/**
	 * Load PriceHistory based on company ticker 
	 * @param company ticker
	 * @return PriceHistory
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override
	public PriceHistory load(String id, String... parms) throws CapIQRequestException, ResponseParserException {
		Assert.notEmpty(parms);		
		return getHistoricalData(id);
	}
	
	public PriceHistory getHistoricalData(String id) throws ResponseParserException, CapIQRequestException {
		return null;
	}
	
}
