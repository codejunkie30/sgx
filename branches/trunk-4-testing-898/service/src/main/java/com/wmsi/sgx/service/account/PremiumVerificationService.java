package com.wmsi.sgx.service.account;

import com.wmsi.sgx.domain.User;

/**
 * The PremiumVerificationService handles operations like create premium token
 * and verify premium token.
 *
 */
public interface PremiumVerificationService{

	/**
	 * Created the premium token for the User information provided 
	 * @param user
	 * @return Returns the premium token
	 */
	String createPremiumToken(User user);

	/**
	 * Verifies the premium token
	 * 
	 * @param token
	 * @return User User information
	 * 
	 * @throws PremiumVerificationException
	 * @throws VerifiedPremiumException
	 */
	User verifyPremiumToken(String token) throws PremiumVerificationException, VerifiedPremiumException;

}