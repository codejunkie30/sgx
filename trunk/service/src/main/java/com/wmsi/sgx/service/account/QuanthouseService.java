package com.wmsi.sgx.service.account;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.model.search.CompanyPrice;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.search.SearchServiceException;

/**
 * The QuanthouseService provides pricing information based on the different
 * input parameters
 *
 */
public interface QuanthouseService {

	/**
	 * Gets intraday price data for the given id within the given market
	 * 
	 * @param market
	 *            Market ID belongs too
	 * @param id
	 *            Local Market identifier
	 * @return The last price
	 * @throws QuanthouseServiceException
	 * @throws CompanyServiceException
	 * @throws SearchServiceException
	 */
	Price getPrice(String market, String id)throws QuanthouseServiceException, CompanyServiceException, SearchServiceException;
	
	/**
	 * Gets intra day prices for all trade events by day.
	 * 
	 * @param market
	 *            - Market ID belongs too
	 * @param id
	 *            - Local Market identifier
	 * @return The prices for given market and ticker code
	 * @throws QuanthouseServiceException
	 */
	List<Price> getIntradayPrices(String market, String id) throws QuanthouseServiceException;
	
	/**
	 * Retrieves the pricing.
	 * 
	 * @param market
	 * @param id
	 * @param date
	 * @return Price
	 * @throws QuanthouseServiceException
	 * @throws CompanyServiceException
	 * @throws SearchServiceException
	 */
	Price getPriceAt(String market, String id, Date date)
			throws QuanthouseServiceException, CompanyServiceException, SearchServiceException;
	
	/**
	 * Retrieves the pricing history.
	 * 
	 * @param market
	 * @param id
	 * @param date
	 * @return
	 * @throws QuanthouseServiceException
	 */
	List<Price> getPricingHistory(String market, String id, Date date)
			throws QuanthouseServiceException;
	
	/**
	 * Retrieves the pricing history between the two companies .
	 * 
	 * @param market
	 * @param id
	 * @param from
	 * @param to
	 * @return
	 * @throws QuanthouseServiceException
	 */
	List<Price> getPricingHistoryBetweenDates(String market, String id, Date from, Date to)
			throws QuanthouseServiceException;
	
	/**
	 * Retrieves the company price.
	 * 
	 * @param companies
	 * @return List of company price information
	 * @throws QuanthouseServiceException
	 * @throws CompanyServiceException
	 * @throws SearchServiceException
	 */
	List<CompanyPrice> getCompanyPrice(List<String> companies)
			throws QuanthouseServiceException, CompanyServiceException, SearchServiceException;	
	
	/**
	 * Retrieves the price change information for the companies
	 * 
	 * @param companies
	 * @return
	 * @throws QuanthouseServiceException
	 * @throws CompanyServiceException
	 * @throws SearchServiceException
	 */
	List<CompanyPrice> getPriceChangeForWatchlistCompanies(List<String> companies)
			throws QuanthouseServiceException, CompanyServiceException,SearchServiceException;
	
	/**
	 * Sets the Currency
	 * 
	 * @param currency
	 */
	void setCurrency(String currency);
}
