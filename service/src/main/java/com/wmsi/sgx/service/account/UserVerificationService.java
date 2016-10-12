package com.wmsi.sgx.service.account;

import javax.transaction.Transactional;

import com.wmsi.sgx.domain.User;

/**
 * This interface creates/verifies the user verification token.
 *
 */
public interface UserVerificationService{

	/**
	 * Creates the verification token.
	 * 
	 * @param user
	 *            User
	 *
	 * @return String
	 *
	 */
  
	String createVerificationToken(User user);

	/**
	 * Verifies token found or not and Verification token redeemed or not.
	 * 
	 * @param token
	 *            String
	 *
	 * @return User
	 *
	 * @throws UserVerificationException,
	 *             VerifiedUserException
	 */
	User verifyToken(String token) throws UserVerificationException, VerifiedUserException;

}