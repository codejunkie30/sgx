package com.wmsi.sgx.service.account;

import javax.transaction.Transactional;

import com.wmsi.sgx.domain.User;

public interface PremiumVerificationService{

	String createPremiumToken(User user);

	User verifyPremiumToken(String token) throws PremiumVerificationException, VerifiedPremiumException;

}