package com.wmsi.sgx.service.quanthouse;

public interface QuanthouseService {

	Double getLastPrice(String market, String id)throws QuanthouseServiceException;
}
