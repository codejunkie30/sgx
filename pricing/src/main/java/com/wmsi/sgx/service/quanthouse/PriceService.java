package com.wmsi.sgx.service.quanthouse;

import java.util.List;

import com.wmsi.sgx.domain.Price;

public interface PriceService{

	Price getPrice(String market, String id);

	List<Price> getPriceHistory(String market, String id);

	void savePrice(Price p);
}
