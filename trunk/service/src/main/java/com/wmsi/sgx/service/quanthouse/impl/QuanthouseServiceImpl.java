package com.wmsi.sgx.service.quanthouse.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSData;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSService;
import com.wmsi.sgx.util.MathUtil;

@Service
public class QuanthouseServiceImpl implements QuanthouseService{

	@Autowired
	private FeedOSService service;
	
	private String marketExtention = "_RY";

	/**
	 * Get the last price for the given id within the given market
	 * @param market - Market ID belongs too
	 * @param id - Local Market identifier
	 * @return The last price
	 * @throws QuanthouseServiceException
	 */
	@Override
	public Double getLastPrice(String market, String id) throws QuanthouseServiceException{
		FeedOSData priceData = service.getPriceData(market, id.concat(marketExtention));
		return priceData.getLastPrice();
	}

	@Override
	public Price getPrice(String market, String id) throws QuanthouseServiceException {
		FeedOSData priceData = service.getPriceData(market, id.concat(marketExtention));
		Price p = new Price();
		
		Double lastPrice = priceData.getLastPrice();
		Double openPrice = priceData.getOpenPrice();
		
		p.setLastPrice(lastPrice);
		p.setOpenPrice(openPrice);
		
		p.setPercentChange(MathUtil.percentChange(openPrice, lastPrice, 4));
		
		return p;
	}	
}
