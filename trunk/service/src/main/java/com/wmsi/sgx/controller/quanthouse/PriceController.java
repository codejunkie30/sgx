package com.wmsi.sgx.controller.quanthouse;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.model.search.input.IdSearch;
import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class PriceController {

	private Logger log = LoggerFactory.getLogger(PriceController.class);
	
	@Autowired
	private QuanthouseService service;
	
	private String market = "XSES";
	
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
}

