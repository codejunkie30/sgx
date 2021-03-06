package com.wmsi.sgx.service.currency.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Currency;
import com.wmsi.sgx.model.CurrencyModel;
import com.wmsi.sgx.repository.Currencies;
import com.wmsi.sgx.service.currency.CurrencyService;

/**
 * The CurrencyService handles operations related to currency
 * 
 */
@Service
public class CurrencyServiceImpl implements CurrencyService {

	@Autowired
	private Currencies currencyRepository;

	/**
	 * Updates the currency
	 * 
	 * @param dto
	 *            CurrencyModel
	 */
	@Override
	public void updateCurrency(CurrencyModel dto) {
		currencyRepository.updateComplete(dto.isCompleted(), dto.getCurrencyName());
	}

	/**
	 * Adds currency model
	 * 
	 * @param currencyModelList
	 * @return
	 */
	@Override
	public boolean addCurrencies(List<CurrencyModel> currencyModelList) {
		List<Currency> currencyDomainList = new ArrayList<Currency>();
		for (CurrencyModel model : currencyModelList) {
			Currency currencyDomain = new Currency();
			BeanUtils.copyProperties(model, currencyDomain);
			currencyDomainList.add(currencyDomain);
		}
		Iterable<Currency> it = currencyRepository.save(currencyDomainList);
		return it.iterator().hasNext();
	}

	/**
	 * Retrieves list of all currencies.
	 * 
	 * @return currencyModelList
	 */
	@Override
	public List<CurrencyModel> getAllCurrencies() {
		List<CurrencyModel> currencyModelList = new ArrayList<CurrencyModel>();
		List<Currency> currencyDomainList = currencyRepository.findAll();
		for (Currency c : currencyDomainList) {
			CurrencyModel m = new CurrencyModel();
			BeanUtils.copyProperties(c, m);
			currencyModelList.add(m);
		}
		return currencyModelList;
	}

	/**
	 * Adds the new currency into currency entity .
	 * 
	 * @param model
	 *            CurrencyModel
	 * @return CurrencyModel
	 */
	@Override
	public CurrencyModel addCurrency(CurrencyModel model) {
		Currency currencyEntityObject = new Currency();
		BeanUtils.copyProperties(model, currencyEntityObject);
		currencyEntityObject = currencyRepository.save(currencyEntityObject);
		BeanUtils.copyProperties(currencyEntityObject, model);
		return model;
	}

	/**
	 * Deletes all the currencies.
	 * 
	 */
	public void deleteAll() {
		currencyRepository.deleteAll();
	}

	/**
	 * Retrieves the count of incomplete currencies.
	 * 
	 * @return int
	 */
	@Override
	public int getCountOfInCompleteCurrenciesCount() {
		return currencyRepository.getCountOfInCompletedCurrencies();
	}

	/**
	 * Retrieves non complete currency information
	 * 
	 * @return CurrencyModel
	 */
	@Override
	public CurrencyModel getNonCompleteCurrency() {
		Currency domain = currencyRepository.getNonCompleteCurrency();
		CurrencyModel currencyModel = new CurrencyModel();
		BeanUtils.copyProperties(domain, currencyModel);
		return currencyModel;
	}

}
