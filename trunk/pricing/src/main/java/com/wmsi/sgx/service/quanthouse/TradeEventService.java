package com.wmsi.sgx.service.quanthouse;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.domain.TradeEvent;

public interface TradeEventService{
	//Get Latest Trade event from DB
	TradeEvent getLatestEvent(String market, String id);

	void saveEvent(TradeEvent p);
	//Get list of all Trade event data for a specific date, market and ticker from DB
	List<TradeEvent> getEventsForDate(String market, String id, Date d);
}
