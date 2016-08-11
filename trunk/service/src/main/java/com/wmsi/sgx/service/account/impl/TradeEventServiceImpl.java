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

@Service
public class TradeEventServiceImpl implements TradeEventService{
	
	@Autowired
	private TradeEventRepository tradeEventRepository;
	
	@Override
	@Transactional
	public void saveEvent(TradeEvent p){
		tradeEventRepository.save(p);
	}
	
	@Override
	public TradeEvent getLatestEvent(String market, String ticker) {
		return tradeEventRepository.findTopByMarketAndTickerOrderByLastTradeTimeDesc(market,ticker);		
	}

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
