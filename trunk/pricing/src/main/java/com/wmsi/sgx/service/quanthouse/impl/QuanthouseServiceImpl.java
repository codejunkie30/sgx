package com.wmsi.sgx.service.quanthouse.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.company.CompanyService;
import com.wmsi.sgx.service.company.CompanyServiceException;
import com.wmsi.sgx.service.quanthouse.PriceService;
import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSData;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSService;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSSubscriptionObserver;

@Service
public class QuanthouseServiceImpl implements QuanthouseService{

	private static final Logger log = LoggerFactory.getLogger(QuanthouseServiceImpl.class);

	@Autowired
	private FeedOSService feedOSService;

	private static final String MARKET_CODE = "XSES";
	private static final String MARKET_EXTENTION = "_RY";

	@Autowired
	private CompanyService companyService;

	@Autowired
	private PriceService priceService;

	@Scheduled(fixedDelay = 60000)
	private void subscribe() throws QuanthouseServiceException {

		log.debug("Subscribing to real time update feed");

		log.debug("Fetching ticker list...");

		List<String> tickers = getTickers();

		log.debug("Found {} tickers for subscription.", tickers != null ? tickers.size() : 0);

		feedOSService.subscribe(MARKET_CODE, tickers, new FeedOSSubscriptionObserver(){

			@Override
			public void subscriptionResponse(List<FeedOSData> data) {

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

	private List<String> getTickers() throws QuanthouseServiceException {

		try{
			List<String> tickers = new ArrayList<String>();

			for(String ticker : companyService.getAllTickers()){
				tickers.add(ticker + MARKET_EXTENTION);
			}
			return tickers;
		}
		catch(CompanyServiceException e){
			throw new QuanthouseServiceException("Failed to load ticker list.");
		}
	}

	private void saveEvent(FeedOSData data) {

		com.wmsi.sgx.domain.Price price = new com.wmsi.sgx.domain.Price();

		price.setTicker(data.getTradingSymbol());
		
		price.setAskPrice(data.getAsk());
		price.setBidPrice(data.getBid());
		price.setClosePrice(data.getClosePrice());
		price.setCurrentDate(data.getCurrentBusinessDay());
		price.setHighPrice(data.getHighPrice());
		price.setLastPrice(data.getLastPrice());
		price.setLastTradePrice(data.getLastTradePrice());
		price.setLastTradeTime(getLastTradeTimestamp(data));
		price.setLastTradeVolume(data.getLastTradeVolume());
		price.setLowPrice(data.getLowPrice());
		price.setOpenPrice(data.getOpenPrice());
		price.setPreviousDate(data.getPreviousBusinessDay());
		price.setTradingCurrency(data.getTradingCurrency());
		price.setVolume(data.getTotalVolume());
		
		priceService.savePrice(price);
	}

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
	/*
	 * @Override
	 * 
	 * @Cacheable(value = "price") public Price getPrice(String market, String
	 * id) throws QuanthouseServiceException { FeedOSData priceData =
	 * feedOSService.getPriceData(market, id.concat(MARKET_EXTENTION)); return
	 * bindPriceData(priceData); }
	 */

	@Override
	// @Cacheable(value = "price")
	public Price getPrice(String market, String id) throws QuanthouseServiceException {
		Price priceData = null;

		/*
		 * for(FeedOSData data : subscribed){
		 * 
		 * if(data.getTradingSymbol().equals(id.concat(MARKET_EXTENTION))){
		 * priceData = bindPriceData(data); break; } }
		 */

		return priceData;

	}

	public List<Price> getIntradayPrices(String market, String id) throws QuanthouseServiceException {

		List<Price> ret = new ArrayList<Price>();

		Price price = getPrice(market, id);

		return ret;
	}

	private Price bindPriceData(FeedOSData data) {
		Price p = new Price();
		p.setLastPrice(data.getLastPrice());
		p.setClosePrice(data.getClosePrice());
		p.setOpenPrice(data.getOpenPrice());
		p.setCurrentDate(data.getCurrentBusinessDay());
		p.setPreviousDate(data.getPreviousBusinessDay());
		p.setLastTradeTimestamp(getLastTradeTimestamp(data));
		p.setTradingCurrency(data.getTradingCurrency());
		p.setVolume(data.getTotalVolume());
		p.setAskPrice(data.getAsk());
		p.setBidPrice(data.getBid());
		p.setHighPrice(data.getHighPrice());
		p.setLowPrice(data.getLowPrice());

		return p;
	}

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
