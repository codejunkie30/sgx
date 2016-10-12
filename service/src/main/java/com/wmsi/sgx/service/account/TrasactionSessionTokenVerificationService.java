package com.wmsi.sgx.service.account;

import java.sql.Timestamp;

import com.wmsi.sgx.domain.User;

/**
 * Creates/validates transaction session token and verifies the token is active or
 * not.
 *
 */

public interface TrasactionSessionTokenVerificationService
{
	/**
	 * Creates the transaction session token.
	 * 
	 * @param user
	 *            User, transSessionToken String
	 * 
	 * @return String
	 * 
	 */
  public String createTransactionSessionToken(User user,String token);

	/**
	 * Validates the transaction session token.
	 * 
	 * @param user
	 *            User, transSessionToken String
	 * 
	 * @return String
	 * 
	 * @throws TransactionSessionTokenVerificationException,
	 *             VerifiedTransactionSessionTokenPremiumException
	 */
  public boolean validateTransactionSessionToken(User user,String token) throws TransactionSessionTokenVerificationException, VerifiedTransactionSessionTokenPremiumException;
  
  /**
   * Deletes the transaction tokens based on user.
   * @param user User
   * @return int
   */
  public int deleteTransactionSessionTokens(User user);
  
	/**
	 * Disables the transaction entries of the user.
	 * 
	 * @param user
	 *            User
	 * @return boolean
	 */
  public boolean disableTransactionSessionToken(User user);
  
	/**
	 * Returns the token expiring time.
	 * 
	 * @return Timestamp
	 * 
	 */
  public Timestamp getTokenExpirationTime();
  
	/**
	 * Verifies the token is expiring or not.
	 * 
	 * @param user
	 *            User, transSessionToken String
	 * 
	 * @return boolean
	 * 
	 */
  public boolean isTokenExpiring(User user, String transSessionToken);
  
}
