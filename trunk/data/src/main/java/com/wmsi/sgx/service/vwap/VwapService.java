package com.wmsi.sgx.service.vwap;

import com.wmsi.sgx.model.VolWeightedAvgPrice;
import com.wmsi.sgx.model.VolWeightedAvgPrices;

public interface VwapService{
	VolWeightedAvgPrices getForTicker(String ticker);
	
}