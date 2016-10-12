package com.wmsi.sgx.service.account;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.model.search.CompanyPrice;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.search.SearchServiceException;

/**
 * Get intraday price data for the given id within the given market and retrieve
 * the pricing history/price for watchlist companies.Set the fall back price
 * also.
 *
 */

public interface QuanthouseService {

  /**
   * Gets intraday price data for the given id within the given market
   * 
   * @param market
   *            - Market ID belongs too
   * @param id
   *            - Local Market identifier
   * @return The last price
   * @throws QuanthouseServiceException
   * @throws CompanyServiceException 
   * @throws SearchServiceException 
   */
	Price getPrice(String market, String id)throws QuanthouseServiceException, CompanyServiceException, SearchServiceException;
	
	/**
	 * Gets intraday prices for all trade events by day.
	 * 
	 * @param market
	 *            - Market ID belongs too
	 * @param id
	 *            - Local Market identifier
	 * @return The prices for
	 * @throws QuanthouseServiceException
	 */
	List<Price> getIntradayPrices(String market, String id) throws QuanthouseServiceException;
	
	/**
	 * Retrieves the pricing.
	 * 
	 * @param market
	 *            String, id String, date Date.
	 * 
	 * @return list
	 * @throws QuanthouseServiceException,
	 *             CompanyServiceException, SearchServiceException
	 * 
	 */
	Price getPriceAt(String market, String id, Date date)
			throws QuanthouseServiceException, CompanyServiceException, SearchServiceException;
	
	/**
	 * Retrieves the pricing history.
	 * 
	 * @param market
	 *            String, id String, date Date.
	 * 
	 * @return list
	 * @throws QuanthouseServiceException
	 * 
	 */
	List<Price> getPricingHistory(String market, String id, Date date)
			throws QuanthouseServiceException;
	
	/**
	 * Retrieves the pricing history between the two companies .
	 * 
	 * @param market
	 *            String, id String, from Date, to Date.
	 * 
	 * @return list
	 * @throws QuanthouseServiceException
	 * 
	 */
	List<Price> getPricingHistoryBetweenDates(String market, String id, Date from, Date to)
			throws QuanthouseServiceException;
	
	/**
	 * Retrieves the company price .
	 * 
	 * @param companies
	 *            List
	 * 
	 * @return list
	 * @throws CompanyServiceException,
	 *             QuanthouseServiceException, SearchServiceException,
	 *             NumberFormatException
	 * 
	 */
	List<CompanyPrice> getCompanyPrice(List<String> companies)
			throws QuanthouseServiceException, CompanyServiceException, SearchServiceException;	
	
	/**
	 * Retrieves the price for watchlist companies.
	 * 
	 * @param companies
	 *            List
	 * 
	 * @return list
	 * @throws CompanyServiceException,
	 *             QuanthouseServiceException, SearchServiceException,
	 *             NumberFormatException
	 * 
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
