package com.wmsi.sgx.service;

import java.util.List;

import com.wmsi.sgx.domain.Asset;
import com.wmsi.sgx.domain.Price;

public interface AssetService{

	List<Asset> save(List<Asset> a);

	Asset save(Asset a);
	Price save(Price p);

	Asset findByTicker(String ticker);

}
