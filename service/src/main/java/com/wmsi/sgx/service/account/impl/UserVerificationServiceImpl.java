package com.wmsi.sgx.service.account.impl;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.UserVerification;
import com.wmsi.sgx.repository.UserVerificationRepository;
import com.wmsi.sgx.security.SecureTokenGenerator;
import com.wmsi.sgx.service.account.UserVerificationException;
import com.wmsi.sgx.service.account.UserVerificationService;
import com.wmsi.sgx.service.account.VerifiedUserException;

/**
 * This class create/verify the user verification token.
 *
 */

@Service
public class UserVerificationServiceImpl implements UserVerificationService{

	@Autowired
	private UserVerificationRepository userVerificationReposistory;

	@Autowired
	private SecureTokenGenerator tokenGenerator;
	
	/**
	 * Creates the verification token.
	 * 
	 * @param user
	 *            User
	 *
	 * @return String
	 *
	 */

	@Override
	@Transactional
	public String createVerificationToken(User user){
		
		UserVerification userVerification = new UserVerification();
		userVerification.setUser(user);
		userVerification.setDate(new Date());		
		userVerification.setToken(tokenGenerator.nextToken());
		
		userVerificationReposistory.save(userVerification);
		
		return userVerification.getToken(); 
	}
	
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
	
	@Override
	@Transactional
	public User verifyToken(String token) throws UserVerificationException, VerifiedUserException{
	
		UserVerification verification = userVerificationReposistory.findByToken(token);
		
		if(verification == null)
			throw new UserVerificationException("Verification token not found.");
		
		User user = verification.getUser();
		
		if(user.getEnabled() || verification.getRedeemed())
			throw new VerifiedUserException("User already activated");
		
		verification.setRedeemed(true);
		userVerificationReposistory.save(verification);
		
		return user;		
	}

}
