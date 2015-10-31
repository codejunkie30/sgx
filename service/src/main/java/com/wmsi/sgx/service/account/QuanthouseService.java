package com.wmsi.sgx.service.account;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.CompanyServiceException;

public interface QuanthouseService {

	Price getPrice(String market, String id)throws QuanthouseServiceException;
	List<Price> getIntradayPrices(String market, String id) throws QuanthouseServiceException;
	Price getPriceAt(String market, String id, Date date)
			throws QuanthouseServiceException, CompanyServiceException;
	List<Price> getPricingHistory(String market, String id, Date date)
			throws QuanthouseServiceException;	
}
