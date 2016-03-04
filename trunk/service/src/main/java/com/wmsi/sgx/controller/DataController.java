package com.wmsi.sgx.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.wmsi.sgx.service.UtilService;

@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class DataController {
	
	@Autowired
	private UtilService utilService;
	
	@Value("${currencies.file.location}")
	private String fileLocation;

	@RequestMapping("currencyList")
	public String getCurrencyList() {
		Map<String, String> map = utilService.convertCurrencyCSVtoMap(fileLocation);
		Gson gson = new Gson(); 
		return gson.toJson(map); 
	}
}
