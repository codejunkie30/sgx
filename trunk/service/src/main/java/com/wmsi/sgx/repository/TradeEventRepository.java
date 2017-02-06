package com.wmsi.sgx.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.wmsi.sgx.domain.TradeEvent;

public interface TradeEventRepository extends CrudRepository<TradeEvent, Long>{

	TradeEvent findTopByMarketAndTickerOrderByLastTradeTimeDesc(String market, String ticker);

	List<TradeEvent> findByMarketAndTickerAndLastTradeTimeAfterOrderByLastTradeTimeDesc(String market, String ticker, Date d);
	List<TradeEvent> findByMarketAndTickerAndLastTradeTimeBetweenOrderByLastTradeTimeDesc(String market, String ticker, Date from, Date to);
	
	@Transactional
	@Modifying
	@Query("delete from TradeEvents")	
	void truncateTradeEvent();
}
