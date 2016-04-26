package com.wmsi.sgx.service.account.impl;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.PremiumVerification;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.repository.PremiumVerificationRepository;
import com.wmsi.sgx.security.SecureTokenGenerator;
import com.wmsi.sgx.service.account.PremiumVerificationException;
import com.wmsi.sgx.service.account.PremiumVerificationService;
import com.wmsi.sgx.service.account.VerifiedPremiumException;

@Service
public class PremiumVerificationServiceImpl implements PremiumVerificationService{

	@Autowired
	private PremiumVerificationRepository premiumVerificationReposistory;

	@Autowired
	private SecureTokenGenerator tokenGenerator;

	@Override
	@Transactional
	public String createPremiumToken(User user){
		
		PremiumVerification premiumVerification = new PremiumVerification();
		premiumVerification.setUser(user);
		premiumVerification.setDate(new Date());		
		premiumVerification.setToken(tokenGenerator.nextToken());
		
		premiumVerificationReposistory.save(premiumVerification);
		
		return premiumVerification.getToken(); 
	}
	
	@Override
	@Transactional
	public User verifyPremiumToken(String token) throws PremiumVerificationException, VerifiedPremiumException{
	
		PremiumVerification verification = premiumVerificationReposistory.findByToken(token);
		
		if(verification == null)
			throw new PremiumVerificationException("Verification token not found.");
		
		User user = verification.getUser();
		
		if(verification.getRedeemed())
			throw new VerifiedPremiumException("Premium token has already been redeemed.");
		
		verification.setRedeemed(true);
		premiumVerificationReposistory.save(verification);
		
		return user;		
	}

}
