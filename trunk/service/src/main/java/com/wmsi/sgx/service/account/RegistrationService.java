package com.wmsi.sgx.service.account;

import javax.mail.MessagingException;

import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.account.UserModel;

public interface RegistrationService{

	void registerUser(UserModel dto) throws UserExistsException, MessagingException;

	void resendVerificationEmail(String username) throws MessagingException;

	Boolean verifyUser(String token) throws UserVerificationException, AccountCreationException, VerifiedUserException;

	Boolean convertToPremiumAccount(UserModel dto);

	Boolean sendPasswordReset(String email) throws UserNotFoundException, MessagingException;

	Boolean resetPassword(ChangePasswordModel user, String resetToken) throws InvalidTokenException, MessagingException;

	Boolean changePassword(UserModel user) throws UserNotFoundException, MessagingException;

}