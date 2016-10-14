package com.wmsi.sgx.service.account;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.domain.TradeEvent;

/**
 * The TradeEventService handles trade events operations.
 *
 */
public interface TradeEventService{

	/**
	 * Returns the Latest Trade Event based on market and ticker.
	 * 
	 * @param market
	 *            String
	 * @param id
	 *            String
	 * @return TradeEvent
	 */
	TradeEvent getLatestEvent(String market, String id);

	/**
	 * Saves the trade event information
	 * 
	 * @param p TradeEvent
	 */
	void saveEvent(TradeEvent p);

	/**
	 * Retrieves trade events for the market, ticker code and date provided
	 * 
	 * @param market
	 * @param id ticker code
	 * @param d trade event date
	 * @return List of trade events
	 */
	List<TradeEvent> getEventsForDate(String market, String id, Date d);
	
	/**
	 * Retrieves trade events for the market, ticker code and the date period
	 * 
	 * @param market
	 * @param id
	 * @param from
	 * @param to
	 * @return List of trade events
	 */
	List<TradeEvent> getEventsForDatesBetween(String market, String id, Date from, Date to);
}
