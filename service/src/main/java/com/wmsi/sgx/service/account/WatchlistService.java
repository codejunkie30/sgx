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
 * Retrieves the watchlist names and also edit/delete/rename watchlist names and
 * add the companies to the watchlist.
 */

public interface WatchlistService {
	
	/**
	 * Creates the Watchlist name and update with the current date.
	 * 
	 * @param user
	 *            User, watchlistName String, watchlistName String
	 * 
	 * @return list
	 */
	List<WatchlistModel> createWatchlist(User user, String watchlistName);
	
	/**
	 * Deletes the watchlist along with the companies and related transactions.
	 * 
	 * @param user
	 *            User, id String
	 * 
	 */
	void deleteWatchlist(User user, String id);
	
	/**
	 * Retrieves the watchlists.
	 * 
	 * @param user
	 *            User.
	 * 
	 * @return list
	 */
	List<WatchlistModel> getWatchlist(User user);
	
	/**
	 * Edits the Watchlsit name.
	 * 
	 * @param user
	 *            User, model WatchlistModel.
	 * 
	 */
	void editWatchlist(User user, WatchlistModel model);
	
	/**
	 * Renames the Watchlist name and update with the current date.
	 * 
	 * @param user
	 *            User, watchlistName String, id String
	 * 
	 */
	void renameWatchlist(User user, String watchlistName, String id);
	
	/**
	 * Adds the companies in watchlist.
	 * 
	 * @param user
	 *            User, addId String,companies List.
	 * 
	 * @return response
	 */
	Response addCompanies(User user, String addId, List<String> companies);
	
	/**
	 * Deletes the watchlist along with the companies and related transactions.
	 * 
	 * @param user
	 *            User
	 * 
	 * @return
	 */
	List<String> cleanWatchlist(User user);
	
	/**
	 * Adds the watch list transactions.
	 * 
	 * @param user
	 *            User, addId String,transactions List.
	 * 
	 * @return response
	 */
	Response addTransactions(User user, String addId, List<WatchlistTransactionModel> transactions);
	
	/**
	 * Retrieves the watchlists.
	 * 
	 * @param id
	 *            Long, ret List.
	 * 
	 */
	Response deleteTransactions(User user,String id, String transactionId);
	
	/**
	 * Retrieves the transactions.
	 * 
	 * @param user
	 *            User, id String
	 * 
	 * @return Map
	 */
	Map<String, List<WatchlistTransactionModel>> getTransactions(User user, String id);
	
	/**
	 * Retrives the watchlists.
	 * 
	 * @param user
	 *            User.
	 * 
	 * @return list
	 */
	CompanyWatchlistTransactionHistoryModel getWatchListTransactions(User user,String id);
	
	/**
	 * Retrieves the stocklist companies.
	 * 
	 * @param id
	 *            String
	 * 
	 * @return AccountModel
	 */
	List<WatchlistCompany> getStockListCompanies(String id);
	
}
