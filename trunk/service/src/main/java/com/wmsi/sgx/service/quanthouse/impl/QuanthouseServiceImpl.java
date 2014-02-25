package com.wmsi.sgx.service.quanthouse.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSData;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSService;
import com.wmsi.sgx.util.MathUtil;

@Service
public class QuanthouseServiceImpl implements QuanthouseService{

	private Logger log = LoggerFactory.getLogger(QuanthouseServiceImpl.class);
			
	@Autowired
	private FeedOSService feedOSService;
	
	private static final String MARKET_EXTENTION = "_RY";

	/**
	 * Get intraday price data for the given id within the given market
	 * @param market - Market ID belongs too
	 * @param id - Local Market identifier
	 * @return The last price
	 * @throws QuanthouseServiceException
	 */
	@Override
	@Cacheable(value = "price")
	public Price getPrice(String market, String id) throws QuanthouseServiceException {
		FeedOSData priceData = feedOSService.getPriceData(market, id.concat(MARKET_EXTENTION));
		return bindPriceData(priceData);
	}	
	
	private Price bindPriceData(FeedOSData data){
		Price p = new Price();
		p.setLastPrice(data.getLastPrice());		
		p.setClosePrice(data.getClosePrice());
		p.setOpenPrice(data.getOpenPrice());
		p.setCurrentDate(data.getCurrentBusinessDay());
		p.setPreviousDate(data.getPreviousBusinessDay());		
		return p;		
	}
}
