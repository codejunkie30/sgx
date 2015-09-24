package com.wmsi.sgx.service.account;

import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.account.UserModel;

public interface RegistrationService{

	void registerUser(UserModel dto) throws UserExistsException;

	void resendVerificationEmail(String username);

	Boolean verifyUser(String token) throws UserVerificationException, AccountCreationException;

	Boolean convertToPremiumAccount(UserModel dto);

	Boolean sendPasswordReset(String email) throws UserNotFoundException;

	Boolean resetPassword(ChangePasswordModel user, String resetToken) throws InvalidTokenException;

	Boolean changePassword(UserModel user) throws UserNotFoundException;

}