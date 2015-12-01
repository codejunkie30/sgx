package com.wmsi.sgx.service.account;

import javax.mail.MessagingException;

import com.wmsi.sgx.model.ApiResponse;
import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.service.account.impl.CreateUserReponse;

public interface RegistrationService{

	CreateUserReponse registerUser(UserModel dto) throws UserExistsException, MessagingException;

	ApiResponse resendVerificationEmail(String username) throws MessagingException;

	Boolean verifyUser(String token) throws UserVerificationException, AccountCreationException, VerifiedUserException;

	Boolean convertToPremiumAccount(UserModel dto);
	
	Boolean convertToExpiredAccount(String email) throws UserNotFoundException;

	Boolean sendPasswordReset(String email) throws UserNotFoundException, MessagingException;

	Boolean resetPassword(ChangePasswordModel user, String resetToken) throws InvalidTokenException, MessagingException;

	Boolean changePassword(UserModel user) throws UserNotFoundException, MessagingException;
	
	void sendVerificationEmail(String email, String token) throws MessagingException;
}