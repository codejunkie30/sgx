package com.wmsi.sgx.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.wmsi.sgx.domain.TradeEvent;

public interface TradeEventRepository extends CrudRepository<TradeEvent, Long>{
	// Find top value by Market code and ticker code ordered by last trade time in descending order
	TradeEvent findTopByMarketAndTickerOrderByLastTradeTimeDesc(String market, String ticker);
	// Find top value by Market code and ticker code ordered by last trade time after a specfic date in descending order
	List<TradeEvent> findByMarketAndTickerAndLastTradeTimeAfterOrderByLastTradeTimeDesc(String market, String ticker, Date d);
}
