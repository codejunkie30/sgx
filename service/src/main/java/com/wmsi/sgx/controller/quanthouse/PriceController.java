package com.wmsi.sgx.controller.quanthouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.quanthouse.InvalidInstrumentException;
import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@Controller
@RequestMapping(value="/{id}", produces="application/json")
public class PriceController {

	private Logger log = LoggerFactory.getLogger(PriceController.class);
	
	@Autowired
	private QuanthouseService service;
	
	private String market = "XSES";
	
	@RequestMapping(value = "price", method = RequestMethod.POST )
	public @ResponseBody Price getPrice(@PathVariable String id) throws QuanthouseServiceException{
		return service.getPrice(market, id);
	}

	@ExceptionHandler(InvalidInstrumentException.class )
	@ResponseStatus(HttpStatus.OK)	
	public @ResponseBody ErrorResponse handleInvalidInstrumentException(InvalidInstrumentException ex) {
		return new ErrorResponse("Invalid ID");
	}

	@ExceptionHandler(Exception.class)
	public @ResponseBody ErrorResponse handleException(Exception e) {
		log.error("Caught exception in PriceController", e);
		return new ErrorResponse("Bad Request");	    
	}
}

