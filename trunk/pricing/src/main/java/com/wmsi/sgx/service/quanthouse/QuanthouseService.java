package com.wmsi.sgx.service.quanthouse;

import java.util.List;

import com.wmsi.sgx.model.Price;

public interface QuanthouseService {
	// Get Price based on market and ticker code
	Price getPrice(String market, String id)throws QuanthouseServiceException;
	//Get intraday Price based on market and ticker code
	List<Price> getIntradayPrices(String market, String id) throws QuanthouseServiceException;	
}
