package com.wmsi.sgx.controller.quanthouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.controller.error.ErrorMessage;
import com.wmsi.sgx.controller.error.ErrorResponse;
import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.quanthouse.InvalidInstrumentException;
import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@RestController
@RequestMapping(value="/{id}", produces="application/json")
public class PriceController {

	private Logger log = LoggerFactory.getLogger(PriceController.class);
	
	@Autowired
	private QuanthouseService service;
	
	private String market = "XSES";
	
	@RequestMapping(value = "price", method = RequestMethod.POST )
	public Price getPrice(@PathVariable String id) throws QuanthouseServiceException{
		return service.getPrice(market, id);
	}

	@ExceptionHandler(InvalidInstrumentException.class )
	@ResponseStatus(HttpStatus.BAD_REQUEST)	
	public @ResponseBody ErrorResponse handleInvalidInstrumentException(InvalidInstrumentException e) {
		log.error("Invalid ID passed to PriceController");
		return new ErrorResponse(new ErrorMessage("Invalid ID", 4001));
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)	
	public ErrorResponse handleException(Exception e) {
		log.error("Caught exception in PriceController", e);
		return new ErrorResponse(new ErrorMessage("Bad Request", 4001));	    
	}
}

