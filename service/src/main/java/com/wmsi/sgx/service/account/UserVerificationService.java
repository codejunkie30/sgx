package com.wmsi.sgx.service.account;

import javax.transaction.Transactional;

import com.wmsi.sgx.domain.User;

/**
 * This UserVerificationService handles operations like creates, verification of
 * the token
 *
 */
public interface UserVerificationService{

	/**
	 * Creates the verification token.
	 * 
	 * @param user
	 * @return Verification token
	 */
	String createVerificationToken(User user);

	/**
	 * Verifies the token is found or not and the token is redeemed or not.
	 * 
	 * @param token
	 * @return
	 * @throws UserVerificationException
	 * @throws VerifiedUserException
	 */
	User verifyToken(String token) throws UserVerificationException, VerifiedUserException;

}