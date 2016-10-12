package com.wmsi.sgx.service.account;

import javax.transaction.Transactional;

import com.wmsi.sgx.domain.User;

/**
 * Verify the PremiumToken and Checks Verification token Redeemed or not and
 * token available or not .
 *
 */

public interface PremiumVerificationService{

	String createPremiumToken(User user);

	User verifyPremiumToken(String token) throws PremiumVerificationException, VerifiedPremiumException;

}