package com.wmsi.sgx.service.quanthouse;

import com.wmsi.sgx.model.Price;

public interface QuanthouseService {

	Double getLastPrice(String market, String id)throws QuanthouseServiceException;
	Price getPrice(String market, String id)throws QuanthouseServiceException;
}
