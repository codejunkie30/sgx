package com.wmsi.sgx.service.account;

import javax.mail.MessagingException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * The AcccountExiprationService handles accounts expiration. 
 *
 */
public interface AcccountExiprationService {

	/**
	 * This method checks account expiration and sends en email if the account
	 * is expired.
	 * 
	 * @throws MessagingException
	 */
	void checkAccountExpiration() throws MessagingException;
	
	/**
	 * This method checks half way account expiration for trial users and sends
	 * an email if the account expiration reached the half way days
	 * 
	 * @throws MessagingException
	 */
	void sendAccountExpirationHalfWayEmail() throws MessagingException;
}
