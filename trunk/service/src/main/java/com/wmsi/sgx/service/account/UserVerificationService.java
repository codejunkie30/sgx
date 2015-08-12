package com.wmsi.sgx.service.account;

import javax.transaction.Transactional;

import com.wmsi.sgx.domain.User;

public interface UserVerificationService{

	String createVerificationToken(User user);

	User verifyToken(String token) throws UserVerificationException;

}