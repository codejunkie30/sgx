package com.wmsi.sgx.service.quanthouse.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.TradeEvent;
import com.wmsi.sgx.repositories.TradeEventRepository;
import com.wmsi.sgx.service.quanthouse.TradeEventService;

@Service
public class TradeEventServiceImpl implements TradeEventService{
	
	@Autowired
	private TradeEventRepository tradeEventRepository;
	
	@Override
	@Transactional
	public void saveEvent(TradeEvent p){
		tradeEventRepository.save(p);
	}
	/**
	 * Get Latest Trade event from DB
	 * @param market code
	 * @param stock ticker
	 * @return TradeEvent
	 */
	@Override
	public TradeEvent getLatestEvent(String market, String ticker) {
		return tradeEventRepository.findTopByMarketAndTickerOrderByLastTradeTimeDesc(market,ticker);		
	}
	
	/**
	 * Get list of all Trade event data for a specific date, market and ticker from DB
	 * @param market code
	 * @param ticker
	 * @param date
	 * @return List of tradeEvents
	 */
	@Override	
	public List<TradeEvent> getEventsForDate(String market, String ticker, Date date) {
		Date d = new DateTime(date).withTimeAtStartOfDay().toDate();
		
		return tradeEventRepository
				.findByMarketAndTickerAndLastTradeTimeAfterOrderByLastTradeTimeDesc
				(
					market, 
					ticker, 
					d
				);
	}
	
}
