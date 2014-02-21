package com.wmsi.sgx.controller.quanthouse;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.quanthouse.InvalidInstrumentException;
import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@Controller
@RequestMapping(value="/{id}", produces="application/json")
public class PriceController {

	@Autowired
	private QuanthouseService service;
	
	@RequestMapping("price")
	public @ResponseBody Price getPrice(@PathVariable String id) throws QuanthouseServiceException{
		return service.getPrice("XSES", id);
	}

	@ExceptionHandler(InvalidInstrumentException.class )
	public @ResponseBody Object handleInvalidInstrumentException(InvalidInstrumentException ex) {
		Map<String, String> err = new HashMap<String, String>();
		err.put("error", "invalid id");		
	    return err;
	}

	@ExceptionHandler(Exception.class )
	public @ResponseBody Object handleException(Exception ex) {
		Map<String, String> err = new HashMap<String, String>();
		err.put("error", "invalid request");
	    return err;
	}
}

