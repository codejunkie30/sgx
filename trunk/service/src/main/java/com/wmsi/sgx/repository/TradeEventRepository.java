package com.wmsi.sgx.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.wmsi.sgx.domain.TradeEvent;

public interface TradeEventRepository extends CrudRepository<TradeEvent, Long>{

	TradeEvent findTopByMarketAndTickerOrderByLastTradeTimeDesc(String market, String ticker);

	List<TradeEvent> findByMarketAndTickerAndLastTradeTimeAfterOrderByLastTradeTimeDesc(String market, String ticker, Date d);
	List<TradeEvent> findByMarketAndTickerAndLastTradeTimeBetweenOrderByLastTradeTimeDesc(String market, String ticker, Date from, Date to);
}
