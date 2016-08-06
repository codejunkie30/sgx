package com.wmsi.sgx.service.quanthouse;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.domain.TradeEvent;

public interface TradeEventService{

	TradeEvent getLatestEvent(String market, String id);

	void saveEvent(TradeEvent p);

	List<TradeEvent> getEventsForDate(String market, String id, Date d);
}
