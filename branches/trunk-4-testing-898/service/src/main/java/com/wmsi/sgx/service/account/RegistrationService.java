package com.wmsi.sgx.service.account;

import javax.mail.MessagingException;

import com.wmsi.sgx.model.ApiResponse;
import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.service.account.impl.CreateUserReponse;

/**
 * The RegistrationService handles operations like user registration,
 * sending the verification email, verifying the user and password change.
 *
 */
public interface RegistrationService{

	/**
	 * Registers the user.
	 * 
	 * @param dto UserModel
	 * @return CreateUserReponse
	 * @throws UserExistsException
	 * @throws MessagingException
	 */
	CreateUserReponse registerUser(UserModel dto) throws UserExistsException, MessagingException;

	/**
	 * Resends the verification mail.
	 * 
	 * @param username
	 * @return ApiResponse
	 * @throws MessagingException
	 */
	ApiResponse resendVerificationEmail(String username) throws MessagingException;

	/**
	 * Verifies the user.
	 * 
	 * @param token
	 * @return true if the user verification is succeeded otherwise false
	 * @throws UserVerificationException
	 * @throws AccountCreationException
	 * @throws VerifiedUserException
	 */
	Boolean verifyUser(String token) throws UserVerificationException, AccountCreationException, VerifiedUserException;

	/**
	 * Converts the user to Premium user Account.
	 * 
	 * @param dto
	 * @return
	 */
	Boolean convertToPremiumAccount(UserModel dto);
	
	/**
	 * Expires the account.
	 * 
	 * @param email
	 * @return Returns true if the expire account conversion is succeeded
	 *         otherwise false
	 * @throws UserNotFoundException
	 */
	Boolean convertToExpiredAccount(String email) throws UserNotFoundException;

	/**
	 * Sends email to reset the password.
	 * 
	 * @param email
	 * @return 
	 * @throws UserNotFoundException
	 * @throws MessagingException
	 */
	Boolean sendPasswordReset(String email) throws UserNotFoundException, MessagingException;

	/**
	 * Resets the password.
	 * 
	 * @param user
	 * @param resetToken
	 * @return true if the password is reset otherwise false
	 * @throws InvalidTokenException
	 * @throws MessagingException
	 */
	Boolean resetPassword(ChangePasswordModel user, String resetToken) throws InvalidTokenException, MessagingException;

	/**
	 * Changes the password.
	 * 
	 * @param user
	 * @return true if the user password is changed otherwise false
	 * @throws UserNotFoundException
	 * @throws MessagingException
	 */
	Boolean changePassword(UserModel user) throws UserNotFoundException, MessagingException;
	
	/**
	 * Sends the verification mail to the user.
	 * 
	 * @param email
	 * @param token
	 * @throws MessagingException
	 */
	void sendVerificationEmail(String email, String token) throws MessagingException;
}