package com.wmsi.sgx.service.quanthouse;

import com.wmsi.sgx.model.Price;

public interface QuanthouseService {

	Price getPrice(String market, String id)throws QuanthouseServiceException;	
}
