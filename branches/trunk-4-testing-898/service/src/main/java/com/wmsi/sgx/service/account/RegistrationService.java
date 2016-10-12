package com.wmsi.sgx.service.account;

import javax.mail.MessagingException;

import com.wmsi.sgx.model.ApiResponse;
import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.service.account.impl.CreateUserReponse;

/**
 * Create/register the user. Verify the expiration date and convert the premium
 * account and send notification mail to the users.
 *
 */


public interface RegistrationService{

	/**
	 * Registers the user.
	 * 
	 * @param dto
	 *            UserModel
	 * 
	 * @return Response
	 * 
	 * @throws UserExistsException,
	 *             MessagingException
	 */
	CreateUserReponse registerUser(UserModel dto) throws UserExistsException, MessagingException;

	/**
	 * Resends the verification mail.
	 * 
	 * @param username
	 *            String
	 * 
	 * @return ApiResponse
	 * 
	 * @throws MessagingException
	 */
	ApiResponse resendVerificationEmail(String username) throws MessagingException;

	/**
	 * Verifies the user.
	 * 
	 * @param token
	 *            String
	 * 
	 * @return Boolean
	 * 
	 * @throws UserVerificationException,
	 *             AccountCreationException, VerifiedUserException
	 */
	Boolean verifyUser(String token) throws UserVerificationException, AccountCreationException, VerifiedUserException;

	/**
	 * Converts to the Premium Account.
	 * 
	 * @param dto
	 *            UserModel
	 * 
	 * @return Boolean
	 * 
	 */
	Boolean convertToPremiumAccount(UserModel dto);
	
	/**
	 * Expires the account.
	 * 
	 * @param email
	 *            String
	 * 
	 * @return Boolean
	 * 
	 * @throws UserNotFoundException
	 */
	Boolean convertToExpiredAccount(String email) throws UserNotFoundException;

	/**
	 * Sends email to for reset the password.
	 * 
	 * @param email
	 *            String
	 * 
	 * @return created
	 * 
	 * @throws UserNotFoundException,
	 *             MessagingException
	 */
	Boolean sendPasswordReset(String email) throws UserNotFoundException, MessagingException;

	/**
	 * Resets the password.
	 * 
	 * @param user
	 *            ChangePasswordModel, resetToken String
	 * 
	 * @return success
	 * 
	 * @throws InvalidTokenException,
	 *             MessagingException
	 */
	Boolean resetPassword(ChangePasswordModel user, String resetToken) throws InvalidTokenException, MessagingException;

	/**
	 * Changes the password.
	 * 
	 * @param user
	 *            UserModel
	 * 
	 * @return success
	 * 
	 * @throws UserNotFoundException,
	 *             MessagingException
	 */
	Boolean changePassword(UserModel user) throws UserNotFoundException, MessagingException;
	
	/**
	 * Sends the verification mail to the user.
	 * 
	 * @param email
	 *            String, token String
	 * 
	 * @throws MessagingException
	 */
	void sendVerificationEmail(String email, String token) throws MessagingException;
}