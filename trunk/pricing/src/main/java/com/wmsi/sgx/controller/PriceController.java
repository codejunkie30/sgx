package com.wmsi.sgx.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.model.search.IdSearch;
import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class PriceController {

	private Logger log = LoggerFactory.getLogger(PriceController.class);
	
	@Autowired
	private QuanthouseService service;
	
	private String market = "XSES";
	
	/**
	 * Get real time Price map based on company ticker and market code 
	 * @param company ticker
	 * @return real time price
	 */
	@RequestMapping(value="/price")
	public Map<String, Price> getPrice(@RequestBody IdSearch query) {
		Price p = new Price();
		
		try{
			p = service.getPrice(market, query.getId());
		}
		catch(QuanthouseServiceException e){
			log.error("Failed to get intra day price from QuanthouseService", e);
		}
		
		Map<String, Price> ret = new HashMap<String, Price>();
		ret.put("price", p);
		return ret;
	}
	
	/**
	 * Get intraday Price map based on company ticker and market code 
	 * @param company ticker
	 * @return intraday real time price
	 */
	@RequestMapping(value="/price/intraday")
	public List<Price> getIntradayPrices(@RequestBody IdSearch query) {
		List<Price> prices = null;
		
		try{
			prices = service.getIntradayPrices(market, query.getId());
			
		}
		catch(QuanthouseServiceException e){
			log.error("Failed to get intra day price from QuanthouseService", e);
		}
		
		return prices;
	}


}

