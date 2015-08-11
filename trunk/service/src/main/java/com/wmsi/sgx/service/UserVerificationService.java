package com.wmsi.sgx.service;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.UserVerification;
import com.wmsi.sgx.repository.UserVerificationRepository;
import com.wmsi.sgx.security.SecureTokenGenerator;

@Service
public class UserVerificationService{

	@Autowired
	private UserVerificationRepository userVerificationReposistory;

	@Autowired
	private SecureTokenGenerator tokenGenerator;

	@Transactional
	public String createVerificationToken(User user){
		
		UserVerification userVerification = new UserVerification();
		userVerification.setUser(user);
		userVerification.setDate(new Date());		
		userVerification.setToken(tokenGenerator.nextToken());
		
		userVerificationReposistory.save(userVerification);
		
		return userVerification.getToken(); 
	}
	
	@Transactional
	public User verifyToken(String token) throws UserVerificationException{
	
		UserVerification verification = userVerificationReposistory.findByToken(token);
		
		if(verification == null)
			throw new UserVerificationException("Verification token not found.");
		
		User user = verification.getUser();
		
		if(user.getEnabled() || verification.getRedeemed())
			throw new UserVerificationException("User already activated");
		
		verification.setRedeemed(true);
		userVerificationReposistory.save(verification);
		
		return user;		
	}

}
