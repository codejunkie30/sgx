package com.wmsi.sgx.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wmsi.sgx.model.CurrencyModel;
import com.wmsi.sgx.repository.Currencies;
import com.wmsi.sgx.service.UtilService;
import com.wmsi.sgx.service.currency.CurrencyService;

@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class DataController {
	
	@Autowired
	private UtilService utilService;
	
	@Value("${currencies.file.location}")
	private String fileLocation;
	
	@Autowired
	private Currencies currencyResponsitory;
	
	@Autowired
	private CurrencyService currencySvc;
	
	@RequestMapping("currencyList")
	public String getCurrencyList() {
		Map<Object, Object> map = utilService.convertCurrencyCSVtoMap(fileLocation);
		
		
		Gson gson = new Gson(); 
		return gson.toJson(getCurrencyMap()); 
	}
	
	private void createCurrencyCSVFile(){
		Gson gson = new Gson();	
		List<CurrencyModel> currencyList = currencySvc.getAllCurrencies();
		java.lang.reflect.Type typeOfSrc = new TypeToken<Collection<CurrencyModel>>(){}.getType();
		gson.toJson(currencyList, typeOfSrc);
	}
	
	private Map<String,String> getCurrencyMap(){
		Gson gson = new Gson();	
		List<CurrencyModel> currencyList = currencySvc.getAllCurrencies();
		Map<String,String>currencyMap = new HashMap<String,String>();
		for(CurrencyModel m : currencyList){
			currencyMap.put(m.getCurrencyName(), m.getDescription());
		}
		return currencyMap;
	}
	

}
