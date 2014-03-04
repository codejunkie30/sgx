package com.wmsi.sgx.controller.quanthouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@RestController
@RequestMapping(value="/price/{id}", produces="application/json")
public class PriceController {

	private Logger log = LoggerFactory.getLogger(PriceController.class);
	
	@Autowired
	private QuanthouseService service;
	
	private String market = "XSES";
	
	@RequestMapping(method = RequestMethod.POST )
	public Price getPrice(@PathVariable String id) throws QuanthouseServiceException{
		return service.getPrice(market, id);
	}
}

