package com.wmsi.sgx.service.account;

import java.util.List;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.WatchlistCompany;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.model.WatchlistTransactionModel;

public interface WatchlistService {
	
	List<WatchlistModel> createWatchlist(User user, String watchlistName);
	void deleteWatchlist(User user, String id);
	List<WatchlistModel> getWatchlist(User user);
	void editWatchlist(User user, WatchlistModel model);
	void renameWatchlist(User user, String watchlistName, String id);
	Response addCompanies(User user, String addId, List<String> companies);
	List<String> cleanWatchlist(User user);
	Response addTransactions(User user, String addId, List<WatchlistTransactionModel> transactions);
	Response deleteTransactions(User user,String id, String transactionId);
	List<WatchlistTransactionModel> getTransactions(User user,String id);
	List<WatchlistCompany> getStockListCompanies(String id);
	
}
