package com.wmsi.sgx.service.vwap;

import com.wmsi.sgx.model.VolWeightedAvgPrices;

public interface VwapService{
	//Get VolWeightedAvgPrices data based on company ticker 
	VolWeightedAvgPrices getForTicker(String ticker);
	
}