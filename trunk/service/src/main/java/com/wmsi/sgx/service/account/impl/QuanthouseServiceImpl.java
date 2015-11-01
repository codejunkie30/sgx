package com.wmsi.sgx.service.account.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.TradeEvent;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.account.QuanthouseService;
import com.wmsi.sgx.service.account.QuanthouseServiceException;
import com.wmsi.sgx.service.account.TradeEventService;

@Service
public class QuanthouseServiceImpl implements QuanthouseService{

	private static final Logger log = LoggerFactory.getLogger(QuanthouseServiceImpl.class);

	private static final String MARKET_CODE = "XSES";
	private static final String MARKET_EXTENTION = "_RY";

	@Autowired
	private CompanyService companyService;

	@Autowired
	private TradeEventService tradeEventService;

	/**
	 * Get intraday price data for the given id within the given market
	 * 
	 * @param market
	 *            - Market ID belongs too
	 * @param id
	 *            - Local Market identifier
	 * @return The last price
	 * @throws QuanthouseServiceException
	 */
	@Override
	// @Cacheable(value = "price")
	public Price getPrice(String market, String id) throws QuanthouseServiceException {
		
		TradeEvent event = tradeEventService.getLatestEvent(market, toMarketId(id));
		
		return bindPriceData(event);				
	}
	
	@Override
	public Price getPriceAt(String market, String id, Date date) throws QuanthouseServiceException, CompanyServiceException{
		List<TradeEvent> event = tradeEventService.getEventsForDate(market, toMarketId(id), date);
		Collections.sort(event);
		if(event.size() > 0){
			for(TradeEvent e : event){
				if(e.getLastTradeTime().compareTo(date) < 0);
					return bindPriceData(event.get(0));
			}
		}
		
		return fallbackPrice(id);
	}

	/**
	 * Get intraday prices for all trade events by day. 
	 * @param market
	 *            - Market ID belongs too
	 * @param id
	 *            - Local Market identifier
	 * @return The prices for
	 * @throws QuanthouseServiceException
	 */
	@Override
	public List<Price> getIntradayPrices(String market, String id) throws QuanthouseServiceException {

		List<Price> ret = new ArrayList<Price>();
		List<TradeEvent> events = tradeEventService.getEventsForDate(market, toMarketId(id), new Date());
		
		for(TradeEvent e : events){
			ret.add(bindPriceData(e));
		}
		
		return ret;
	}
	
	@Override
	public List<Price> getPricingHistory(String market, String id, Date date) throws QuanthouseServiceException {

		List<Price> ret = new ArrayList<Price>();
		List<TradeEvent> events = tradeEventService.getEventsForDate(market, toMarketId(id), date);
		
		for(TradeEvent e : events){
			ret.add(bindPriceData(e));
		}
		
		return ret;
	}
	
	private String toMarketId(String id){
		return id.concat(MARKET_EXTENTION);
	}
	
	private Price fallbackPrice(String id) throws CompanyServiceException{
		Price p = new Price();
		Company comp = companyService.getById(id);
		p.setClosePrice(comp.getClosePrice());
		p.setOpenPrice(comp.getOpenPrice());
		p.setCurrentDate(new Date());
		p.setPreviousDate(comp.getPreviousCloseDate());
		p.setLastTradeVolume(comp.getVolume());
		p.setLowPrice(comp.getLowPrice());
		p.setHighPrice(comp.getHighPrice());
		
		return p;
		
	}

	private Price bindPriceData(TradeEvent data) {
		Price p = new Price();
		p.setLastPrice(data.getLastPrice());
		p.setClosePrice(data.getClosePrice());
		p.setOpenPrice(data.getOpenPrice());
		p.setCurrentDate(data.getCurrentDate());
		p.setPreviousDate(data.getPreviousDate());
		p.setLastTradeTimestamp(data.getLastTradeTime());
		p.setLastTradeVolume(data.getLastTradeVolume());
		p.setTradingCurrency(data.getTradingCurrency());
		p.setVolume(data.getVolume());
		p.setAskPrice(data.getAskPrice());
		p.setBidPrice(data.getBidPrice());
		p.setHighPrice(data.getHighPrice());
		p.setLowPrice(data.getLowPrice());

		return p;
	}

}
