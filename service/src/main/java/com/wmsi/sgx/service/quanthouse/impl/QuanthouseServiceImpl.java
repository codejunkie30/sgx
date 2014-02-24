package com.wmsi.sgx.service.quanthouse.impl;

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

	private FeedOSService service;
	
	@Autowired
	public void setFeedOSService(FeedOSService s){service = s;}
	
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
		FeedOSData priceData = service.getPriceData(market, id.concat(MARKET_EXTENTION));
		return bindPriceData(priceData);
	}	
	
	private Price bindPriceData(FeedOSData data){
		
		Double lastPrice = data.getLastPrice();
		Double closePrice = data.getClosePrice();
		
		Price p = new Price();
		p.setLastPrice(lastPrice);		
		p.setClosePrice(closePrice);
		p.setOpenPrice(data.getOpenPrice());
		p.setCurrentDate(data.getCurrentBusinessDay());
		p.setPreviousDate(data.getPreviousBusinessDay());
		p.setPercentChange(MathUtil.percentChange(closePrice, lastPrice, 2));
		p.setChange(MathUtil.change(closePrice, lastPrice, 3));		
		
		return p;		
	}
}
