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
	 *            User
	 * @param token
	 *            Token
	 * @return String Transaction token
	 */
	public String createTransactionSessionToken(User user, String token);

	/**
	 * Validates the transaction session token.
	 * 
	 * @param user
	 *            User
	 * @param token
	 *            String
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
	 *            User
	 * @return no of tokens deleted
	 */
	public int deleteTransactionSessionTokens(User user);

	/**
	 * Disables the transaction token entries of the user.
	 * 
	 * @param user
	 *            User
	 * @return Returns true if the transaction token is diabled otherwise false
	 */
	public boolean disableTransactionSessionToken(User user);

	/**
	 * Returns the token expiration time.
	 * 
	 * @return Timestamp Token expiration time
	 */
	public Timestamp getTokenExpirationTime();

	/**
	 * Verifies whether the token is expired or not.
	 * 
	 * @param user
	 *            User
	 * @param transSessionToken
	 *            String
	 * @return Returns true if the token is expired otherwise false
	 */
	public boolean isTokenExpiring(User user, String transSessionToken);

}
