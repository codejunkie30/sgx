package com.wmsi.sgx.service.account;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.domain.TradeEvent;

/**
 * Retrieves the events for the date or arket, Tickercode between LastTradeTime
 * values.
 *
 */

public interface TradeEventService{

	/**
	 * Returns the Latest Trade Event based on market and ticker.
	 * 
	 * @param market
	 *            String
	 * @param ticker
	 *            String
	 * @return TradeEvent
	 */
	TradeEvent getLatestEvent(String market, String id);

	void saveEvent(TradeEvent p);

	/**
	 * Retrieves events for the date
	 * 
	 * @param market
	 *            String, ticker String, date Date
	 * 
	 * @return list
	 * 
	 */
	
	List<TradeEvent> getEventsForDate(String market, String id, Date d);
	
	/**
	 * Retrieves Market, Tickercode between LastTradeTime
	 * 
	 * @param market
	 *            String, ticker String, from Date, to Date
	 * 
	 * @return list
	 * 
	 */
	List<TradeEvent> getEventsForDatesBetween(String market, String id, Date from, Date to);
}
