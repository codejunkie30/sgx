package com.wmsi.sgx.service.account;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.AlertOption;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.search.SearchServiceException;

public interface WatchlistEmailService {

	/*void getWatchlistEmails()
			throws QuanthouseServiceException, CompanyServiceException,
			SearchServiceException, MessagingException;*/

	void getEmailsForUser(User usr) throws QuanthouseServiceException,
			CompanyServiceException, SearchServiceException, MessagingException;
	
	 List<?> parseWatchlist(WatchlistModel watchlist, Account acct) 
			 throws QuanthouseServiceException, CompanyServiceException, SearchServiceException;

}
