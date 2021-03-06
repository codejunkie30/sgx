package com.wmsi.sgx.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.wmsi.sgx.domain.Currency;

public interface Currencies extends CustomRepository<Currency, Serializable> {

	List<Currency> findAll();
	
	@Transactional
	@Modifying
	@Query("update Currencies set complete = :complete where currency_name = :currency_name")
	void updateComplete(@Param("complete") boolean complete,@Param("currency_name")String currencyName);
	
	@Query("select count(*) from Currencies where complete=1")
	int getCountOfInCompletedCurrencies();
	
	@Query(value="select  top 1 *  from Currencies where complete=0", nativeQuery = true)
	Currency getNonCompleteCurrency();

}
