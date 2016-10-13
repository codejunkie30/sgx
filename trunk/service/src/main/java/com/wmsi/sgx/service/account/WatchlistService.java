package com.wmsi.sgx.service.account;

import java.util.List;
import java.util.Map;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.WatchlistCompany;
import com.wmsi.sgx.model.CompanyWatchlistTransactionHistoryModel;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.model.WatchlistTransactionModel;

/**
 * The WatchlistService handles operations on User WatchList like edit, delete, rename WatchList names
 * 
 */
public interface WatchlistService {
	
	/**
	 * Creates the Watch list name and update with the current date.
	 * 
	 * @param user
	 *            User
	 * @param watchlistName
	 *            String
	 * @return List of WatchlistModel
	 */
	List<WatchlistModel> createWatchlist(User user, String watchlistName);
	
	/**
	 * Deletes the watch list along with the companies and related transactions.
	 * 
	 * @param user User
	 * @param id String
	 */
	void deleteWatchlist(User user, String id);
	
	/**
	 * Retrieves the watch lists.
	 * 
	 * @param user
	 *            User
	 * @return List of WatchlistModel
	 */
	List<WatchlistModel> getWatchlist(User user);
	
	/**
	 * Edits the Watch list name.
	 * 
	 * @param user
	 *            User
	 * @param model
	 *            WatchlistModel
	 */
	void editWatchlist(User user, WatchlistModel model);
	
	/**
	 * Renames the Watch list name and update with the current date.
	 * 
	 * @param user
	 *            User
	 * @param watchlistName
	 *            String
	 * @param id
	 *            String
	 */
	void renameWatchlist(User user, String watchlistName, String id);
	
	/**
	 * Adds the companies in watchlist.
	 * 
	 * @param user
	 *            User
	 * @param addId
	 *            String
	 * @param companies
	 *            List of companies
	 * @return Response
	 */
	Response addCompanies(User user, String addId, List<String> companies);
	
	/**
	 * Deletes the watchlist along with the companies and related transactions.
	 * 
	 * @param user
	 *            User
	 * 
	 * @return List
	 */
	List<String> cleanWatchlist(User user);
	
	/**
	 * Adds the watch list transactions.
	 * 
	 * @param user User
	 * @param addId String
	 * @param transactions List of WatchlistTransactionModel
	 * @return Response
	 */
	Response addTransactions(User user, String addId, List<WatchlistTransactionModel> transactions);
	
	/**
	 * Deletes the transaction for the user's watch list.
	 * 
	 * @param user User
	 * @param id String
	 * @param transactionId String
	 * @return Response
	 */
	Response deleteTransactions(User user,String id, String transactionId);
	
	/**
	 * Retrieves the transactions.
	 * 
	 * @param user User
	 * @param id String
	 * @return Map
	 */
	Map<String, List<WatchlistTransactionModel>> getTransactions(User user, String id);
	
	/**
	 * Retrieves the transactions for the watch list.
	 * 
	 * @param user User
	 * @param id String
	 * @return CompanyWatchlistTransactionHistoryModel
	 */
	CompanyWatchlistTransactionHistoryModel getWatchListTransactions(User user,String id);
	
	/**
	 * Retrieves the Stock list/Watch list companies.
	 * 
	 * @param id String
	 * @return List Watch/Stock list companies
	 */
	List<WatchlistCompany> getStockListCompanies(String id);
	
}
