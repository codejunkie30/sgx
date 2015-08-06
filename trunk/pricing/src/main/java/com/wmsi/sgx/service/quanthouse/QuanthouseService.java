package com.wmsi.sgx.service.quanthouse;

import java.util.List;

import com.wmsi.sgx.model.Price;

public interface QuanthouseService {

	Price getPrice(String market, String id)throws QuanthouseServiceException;
	List<Price> getIntradayPrices(String market, String id) throws QuanthouseServiceException;	
}
