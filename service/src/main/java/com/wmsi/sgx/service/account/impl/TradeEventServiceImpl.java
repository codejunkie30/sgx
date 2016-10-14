package com.wmsi.sgx.service.account.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.TradeEvent;
import com.wmsi.sgx.repository.TradeEventRepository;
import com.wmsi.sgx.service.account.TradeEventService;

/**
 * The TradeEventServiceImpl class handles trade events operations.
 *
 */
@Service
public class TradeEventServiceImpl implements TradeEventService{
	
	@Autowired
	private TradeEventRepository tradeEventRepository;
	
	/**
	 * Saves the trade event information
	 * 
	 * @param p TradeEvent
	 */
	@Override
	@Transactional
	public void saveEvent(TradeEvent p){
		tradeEventRepository.save(p);
	}
	
	/**
	 * Returns the Latest Trade Event based on market and ticker.
	 * 
	 * @param market
	 *            String
	 * @param ticker
	 *            String
	 * @return TradeEvent
	 */
	@Override
	public TradeEvent getLatestEvent(String market, String ticker) {
		return tradeEventRepository.findTopByMarketAndTickerOrderByLastTradeTimeDesc(market,ticker);		
	}
	
	/**
	 * Retrieves trade events for the market, ticker code and date provided
	 * 
	 * @param market
	 * @param ticker ticker code
	 * @param date trade event date
	 * @return List of trade events
	 */
	@Override	
	public List<TradeEvent> getEventsForDate(String market, String ticker, Date date) {
		Date d = new DateTime(date).toDate();
		
		return tradeEventRepository
				.findByMarketAndTickerAndLastTradeTimeAfterOrderByLastTradeTimeDesc
				(
					market, 
					ticker, 
					d
				);
	}
	
	/**
	 * Retrieves trade events for the market, ticker code and the date period
	 * 
	 * @param market
	 * @param ticker
	 * @param from
	 * @param to
	 * @return List of trade events
	 */
	@Override	
	public List<TradeEvent> getEventsForDatesBetween(String market, String ticker, Date from, Date to) {
		Date d = new DateTime(from).toDate();
		
		return tradeEventRepository
				.findByMarketAndTickerAndLastTradeTimeBetweenOrderByLastTradeTimeDesc
				(
					market, 
					ticker, 
					from,
					to
				);
	}
	
}
