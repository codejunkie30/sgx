package com.wmsi.sgx.service.currency;

import java.util.List;

import com.wmsi.sgx.model.CurrencyModel;

/**
 * The CurrencyService handles operations related to currency
 * 
 */
public interface CurrencyService {

	/**
	 * Updates the currency
	 * 
	 * @param dto CurrencyModel
	 */
	void updateCurrency(CurrencyModel dto);

	/**
	 * Adds currency model
	 * 
	 * @param currencyModelList
	 * @return
	 */
	boolean addCurrencies(List<CurrencyModel> currencyModelList);

	/**
	 * Retrieves list of all currencies.
	 * 
	 * @return currencyModelList
	 */
	List<CurrencyModel> getAllCurrencies();

	/**
	 * Retrieves the count of incomplete currencies.
	 * 
	 * @return int
	 */
	int getCountOfInCompleteCurrenciesCount();

	/**
	 * Adds the currency into currency entity .
	 * 
	 * @param model
	 *            CurrencyModel
	 * @return CurrencyModel
	 */
	CurrencyModel addCurrency(CurrencyModel model);

	/**
	 * Deletes all the currencies.
	 * 
	 */
	void deleteAll();

	/**
	 * Retrieves non complete currency information
	 * 
	 * @return CurrencyModel
	 */
	CurrencyModel getNonCompleteCurrency();
}
