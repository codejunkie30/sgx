package com.wmsi.sgx.service.quanthouse.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.TradeEvent;
import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.company.CompanyService;
import com.wmsi.sgx.service.company.CompanyServiceException;
import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;
import com.wmsi.sgx.service.quanthouse.TradeEventService;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSData;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSService;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSSubscriptionObserver;

@Service
public class QuanthouseServiceImpl implements QuanthouseService{

	private static final Logger log = LoggerFactory.getLogger(QuanthouseServiceImpl.class);

	private static final String MARKET_CODE = "XSES";
	private static final String MARKET_EXTENTION = "";

	@Autowired
	private FeedOSService feedOSService;

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
	
	private String toMarketId(String id){
		//return id.concat(MARKET_EXTENTION);\
		return id;
	}
	
	/**
	 * Create Price Object from TradeEvent Object 
	 * @param TradeEvent data
	 * @return Price Object
	 */
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
	
	/**
	 * Subscribe to Quanthouse service for real time update feed
	 * @throws QuanthouseServiceException
	 */
	@Scheduled(fixedDelayString= "${quanthouse.subscription.time}")
	private void subscribe() throws QuanthouseServiceException {

		log.debug("Subscribing to real time update feed");

		log.debug("Fetching ticker list...");

		List<String> tickers = getTickers();

		log.debug("Found {} tickers for subscription.", tickers != null ? tickers.size() : 0);

		feedOSService.subscribe(MARKET_CODE, tickers, new FeedOSSubscriptionObserver(){

			@Override
			public void subscriptionResponse(List<FeedOSData> data) {

				// TODO - Remove this, for testing only
				
				for(FeedOSData d : data)
					saveEvent(d);
				
				log.debug("Subscribed to {} tickers.", data != null ? data.size() : 0);
			}

			@Override
			public void tradeEvent(FeedOSData data) {
				
				saveEvent(data);
				
			}
		});
	}
	
	/**
	 * Get all valid tickers
	 * @param 
	 * @return List of tickers
	 * @throws QuanthouseServiceException
	 */
	private List<String> getTickers() throws QuanthouseServiceException {

		try{
			List<String> tickers = new ArrayList<String>();

			for(String ticker : companyService.getAllTickers()){
				//tickers.add(ticker + MARKET_EXTENTION);
				tickers.add(ticker);
			}
			return tickers;
		}
		catch(CompanyServiceException e){
			throw new QuanthouseServiceException("Failed to load ticker list.");
		}
	}
	
	/**
	 * Save FeedOSData received from Quanthouse rest service
	 * @param FeedOSData
	 * @return 
	 */
	private void saveEvent(FeedOSData data) {

		TradeEvent event = new TradeEvent();

		event.setTicker(data.getTradingSymbol());
		
		event.setAskPrice(data.getAsk());
		event.setBidPrice(data.getBid());
		event.setClosePrice(data.getClosePrice());
		event.setCurrentDate(data.getCurrentBusinessDay());
		event.setHighPrice(data.getHighPrice());
		event.setLastPrice(data.getLastPrice());
		event.setLastTradePrice(data.getLastTradePrice());
		event.setLastTradeTime(getLastTradeTimestamp(data));
		event.setLastTradeVolume(data.getLastTradeVolume());
		event.setLowPrice(data.getLowPrice());
		event.setOpenPrice(data.getOpenPrice());
		event.setPreviousDate(data.getPreviousBusinessDay());
		event.setTradingCurrency(data.getTradingCurrency());
		event.setVolume(data.getTotalVolume());
		event.setMarket(data.getMarket());
		tradeEventService.saveEvent(event);
	}
	/**
	 * Get Last Trade Timestamp from FeedOSData received from Quanthouse rest service
	 * @param FeedOSData data
	 * @return Date
	 */
	private Date getLastTradeTimestamp(FeedOSData data) {
		Date lastTrade = data.getLastTradeTimestamp();
		Date lastOffBookTrade = data.getLastOffBookTradeTimestamp();

		if(lastTrade != null && lastOffBookTrade != null){
			// Is the last off book trade date later than the lastTrade date?
			if(lastOffBookTrade.compareTo(lastTrade) > 0)
				lastTrade = lastOffBookTrade;
		}
		else if(lastTrade == null && lastOffBookTrade != null)
			// Sanity check, not sure if lastTrade can ever be null and
			// lastOffBook non null.
			lastTrade = lastOffBookTrade;

		return lastTrade;
	}	

}
