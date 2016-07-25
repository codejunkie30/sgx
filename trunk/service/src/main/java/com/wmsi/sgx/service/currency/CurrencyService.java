package com.wmsi.sgx.service.currency;

import java.util.List;

import com.wmsi.sgx.model.CurrencyModel;

public interface CurrencyService {

	void updateCurrency(CurrencyModel dto);
	
	boolean addCurrencies(List<CurrencyModel> currencyModelList);
	
	List<CurrencyModel> getAllCurrencies();
	
	int getCountOfInCompleteCurrenciesCount();

	CurrencyModel addCurrency(CurrencyModel model);
	
	void deleteAll();
	
	CurrencyModel getNonCompleteCurrency();
}
