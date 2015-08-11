package com.wmsi.sgx.service;

import com.wmsi.sgx.dto.UserDTO;

public interface RegistrationService{

	void registerUser(UserDTO dto) throws UserExistsException;

	void resendVerificationEmail(String username);

	Boolean verifyUser(String token) throws UserVerificationException, AccountCreationException;

	Boolean convertToPremiumAccount(UserDTO dto);

	Boolean sendPasswordReset(String email) throws UserNotFoundException;

	Boolean resetPassword(UserDTO user, String resetToken) throws InvalidTokenException;

	Boolean changePassword(UserDTO user) throws UserNotFoundException;

}