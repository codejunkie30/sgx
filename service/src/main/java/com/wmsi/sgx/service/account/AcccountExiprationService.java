package com.wmsi.sgx.service.account;

import javax.mail.MessagingException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Verifies the account expiration and send the 
 * trail users expired email with subject and email body.
 *
 */

public interface AcccountExiprationService {

  /**
   * This method checks account expiration and account should be active and set the currency with SGD.
   * 
   * @throws MessagingException
   */
  
	void checkAccountExpiration() throws MessagingException;
	
	/**
   * This method checks account expiration for trial users.
   * 
   * @throws MessagingException
   */
	void sendAccountExpirationHalfWayEmail() throws MessagingException;
}
