package com.wmsi.sgx.service.currency;

import java.util.List;

import com.wmsi.sgx.model.CurrencyModel;

/**
 * Retrieves the existing currencies from the database and the new currencies
 * will adding to the currency entity.
 * 
 *
 */

public interface CurrencyService {

	/**
	 * Updates the currency values.
	 * 
	 * @param dto
	 *            CurrencyModel
	 * 
	 */
  void updateCurrency(CurrencyModel dto);
	
	/**
	 * Adds the new currencies into currencies list.
	 * 
	 * @param currencyModelList
	 *            List
	 * 
	 * @return boolean
	 */
	boolean addCurrencies(List<CurrencyModel> currencyModelList);
	
	/**
	 * Retrieves the all the currencies.
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
	 * Adds the new currency into currency entity .
	 * 
	 * @param model
	 *            CurrencyModel
	 * 
	 * @return CurrencyModel
	 */
	CurrencyModel addCurrency(CurrencyModel model);
	
	/**
	 * Deletes all the currencies.
	 * 
	 */
	void deleteAll();
	
	CurrencyModel getNonCompleteCurrency();
}
