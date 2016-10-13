package com.wmsi.sgx.service.account;

import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang3.tuple.MutablePair;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.AlertOption;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.search.SearchServiceException;

/**
 * The WatchlistEmailService handles operations related to watch list email.
 *
 */
public interface WatchlistEmailService {

	/**
	 * Retrieves the account information for the user and sends email if the
	 * Alert options are matched
	 * 
	 * @param usr
	 *            User
	 * @throws QuanthouseServiceException
	 * @throws CompanyServiceException
	 * @throws SearchServiceException
	 * @throws MessagingException
	 */
	void getEmailsForUser(User usr) throws QuanthouseServiceException,
			CompanyServiceException, SearchServiceException, MessagingException;
	
	/**
	 * Parses the watchlist items and verifies the
	 * priceOptions,volumeOptions,weekOptions,targetPriceOptions and
	 * consensusRecOptions
	 * 
	 * @param watchlist
	 *            WatchlistModel
	 * @param acct
	 *            Account
	 * @return AlertOption information
	 * @throws QuanthouseServiceException
	 * @throws CompanyServiceException
	 * @throws SearchServiceException
	 */
	MutablePair<List<AlertOption>, List<String>> parseWatchlist(WatchlistModel watchlist, Account acct) 
			 throws QuanthouseServiceException, CompanyServiceException, SearchServiceException;

}
