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
 * Retrieves the watchlist related email and send the notifications as well.
 * Alert the companies and keydev info.
 *
 */

public interface WatchlistEmailService {

	/**
	 * Retrieves the emails for the user
	 * 
	 * @param usr
	 *            User
	 * 
	 * @throws QuanthouseServiceException,
	 *             CompanyServiceException, SearchServiceException,
	 *             MessagingException
	 */
	void getEmailsForUser(User usr) throws QuanthouseServiceException,
			CompanyServiceException, SearchServiceException, MessagingException;
	
	/**
	 * Pasrses the watchlist items and verifies the
	 * priceOptions,volumeOptions,weekOptions,targetPriceOptions and
	 * consensusRecOptions
	 * 
	 * @param watchlist
	 *            WatchlistModel, acct Account
	 * 
	 * @return list
	 * 
	 * @throws QuanthouseServiceException,
	 *             CompanyServiceException, SearchServiceException
	 */
	MutablePair<List<AlertOption>, List<String>> parseWatchlist(WatchlistModel watchlist, Account acct) 
			 throws QuanthouseServiceException, CompanyServiceException, SearchServiceException;

}
