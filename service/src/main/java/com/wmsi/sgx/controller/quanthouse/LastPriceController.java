package com.wmsi.sgx.controller.quanthouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@Controller
public class LastPriceController {

	@Autowired
	private QuanthouseService service;
	
	@RequestMapping("lastPrice")
	public @ResponseBody Double lastPrice() throws QuanthouseServiceException{
		return service.getLastPrice("XSES", "C6L");
	}
}

