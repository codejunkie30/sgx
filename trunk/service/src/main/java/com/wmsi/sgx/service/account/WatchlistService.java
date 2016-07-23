package com.wmsi.sgx.service.account;

import java.util.List;
import java.util.Map;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.WatchlistCompany;
import com.wmsi.sgx.model.CompanyWatchlistTransactionHistoryModel;
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
	Map<String, List<WatchlistTransactionModel>> getTransactions(User user, String id);
	CompanyWatchlistTransactionHistoryModel getWatchListTransactions(User user,String id);
	List<WatchlistCompany> getStockListCompanies(String id);
	
}
