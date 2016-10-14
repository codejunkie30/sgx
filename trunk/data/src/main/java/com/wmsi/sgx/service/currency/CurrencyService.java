package com.wmsi.sgx.service.currency;

import java.util.List;

import com.wmsi.sgx.model.CurrencyModel;
/**
 * Currency Service methods
 * These methods keep track of the multiple currency loads in database
 */
public interface CurrencyService {
	
	// Updates currency in the currency repository
	void updateCurrency(CurrencyModel dto);
	
	// add currency in the currency repository
	boolean addCurrencies(List<CurrencyModel> currencyModelList);
	
	// retrieve currency from currency repository/DB
	List<CurrencyModel> getAllCurrencies();
	
	// get count of all the currencies which completed the data load in ES
	int getCountOfCurrenciesToComplete();
	
	// Updates the currency model by saving the new currency 
	CurrencyModel addCurrency(CurrencyModel model);
	
	// delete all currencies
	void deleteAll();
	
	// retrieves next currency
	CurrencyModel getNextCurrency();
	
	// deletes currency list
	public void deleteCurrenciesList(List<CurrencyModel> currencyModelList);
	
	// resets currency completed download flag
	public void resetCompletedFlag();
}
