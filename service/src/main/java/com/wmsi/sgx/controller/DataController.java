package com.wmsi.sgx.controller;

import java.util.ArrayList;
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
	
	@Value ("${spring.profiles.active:dummy}")
	private String envType;

	@RequestMapping("currencyList")
	public String getCurrencyList() {
		//Map<Object, Object> map = utilService.convertCurrencyCSVtoMap(fileLocation);
		
		
		Gson gson = new Gson(); 
		return gson.toJson(getCurrencyMap()); 
	}
	
	private void createCurrencyCSVFile(){
		Gson gson = new Gson();	
		List<CurrencyModel> currencyList = currencySvc.getAllCurrencies();
		java.lang.reflect.Type typeOfSrc = new TypeToken<Collection<CurrencyModel>>(){}.getType();
		gson.toJson(currencyList, typeOfSrc);
	}
	
	private ArrayList getCurrencyMap(){
		Gson gson = new Gson();	
		List<CurrencyModel> currencyList = currencySvc.getAllCurrencies();
		ArrayList currencyResponseList = new ArrayList();
		currencyResponseList.add(0,new HashMap<String,String>());
		int i=0;
		for(CurrencyModel m : currencyList){
			Map<String,String>currencyMap = new HashMap<String,String>();
			String currencyName = m.getCurrencyName().substring(0, m.getCurrencyName().lastIndexOf("premium")-1).toLowerCase();
			currencyMap.put("id",currencyName);
			currencyMap.put("name",m.getDescription());
			if(currencyName.equalsIgnoreCase("sgd")){
				currencyResponseList.remove(0);
				currencyResponseList.add(0,currencyMap);
			}else{
				++i;
				currencyResponseList.add(i,currencyMap);
			}
		}
		return currencyResponseList;
	}
	
	@RequestMapping("environmentType")
	public String getEnvironment() {
		switch (envType) {
		case ("prod-sing"):
			return "PROD";
		case ("prod-us"):
			return "QA";
		default:
			return "DEV";
		}

	}

}
