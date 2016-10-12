package com.wmsi.sgx.service.account;

import java.sql.Timestamp;

import com.wmsi.sgx.domain.User;

/**
 * The TrasactionSessionTokenVerificationService handles operations like create,
 * validates, verify, delete and disable transaction session token
 *
 */
public interface TrasactionSessionTokenVerificationService {
	/**
	 * Creates the transaction session token.
	 * 
	 * @param user
	 * @param token
	 * @return Transaction token
	 */
	public String createTransactionSessionToken(User user, String token);

	/**
	 * Validates the transaction session token.
	 * 
	 * @param user
	 * @param token
	 * @return Returns true if the transaction token is valid otherwise false
	 * @throws TransactionSessionTokenVerificationException
	 * @throws VerifiedTransactionSessionTokenPremiumException
	 */
	public boolean validateTransactionSessionToken(User user, String token)
			throws TransactionSessionTokenVerificationException, VerifiedTransactionSessionTokenPremiumException;

	/**
	 * Deletes the transaction tokens based on user.
	 * 
	 * @param user
	 * @return no of tokens deleted
	 */
	public int deleteTransactionSessionTokens(User user);

	/**
	 * Disables the transaction token entries of the user.
	 * 
	 * @param user
	 * @return
	 */
	public boolean disableTransactionSessionToken(User user);

	/**
	 * Returns the token expiration time.
	 * 
	 * @return
	 */
	public Timestamp getTokenExpirationTime();

	/**
	 * Verifies whether the token is expired or not.
	 * 
	 * @param user
	 * @param transSessionToken
	 * @return
	 */
	public boolean isTokenExpiring(User user, String transSessionToken);

}
