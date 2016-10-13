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

/**
 * The PremiumVerificationServiceImpl class verify the PremiumToken and Checks Verification token Redeemed or
 * not and token available or not .
 *
 */
@Service
public class PremiumVerificationServiceImpl implements PremiumVerificationService{

	@Autowired
	private PremiumVerificationRepository premiumVerificationReposistory;

	@Autowired
	private SecureTokenGenerator tokenGenerator;
	
	/**
	 * Created the premium token for the User information provided 
	 * @param user
	 * @return Returns the premium token
	 */
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
	
	/**
	 * Verifies the premium token
	 * 
	 * @param token
	 * @return User User information
	 * 
	 * @throws PremiumVerificationException
	 * @throws VerifiedPremiumException
	 */
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
