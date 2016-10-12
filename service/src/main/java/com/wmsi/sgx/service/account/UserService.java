package com.wmsi.sgx.service.account;

import java.util.Set;

import org.springframework.security.authentication.AccountExpiredException;

import com.wmsi.sgx.domain.Authority;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.UserLogin;
import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.account.UserModel;

/**
 * Creates the user and save the user details in database. Verify the account is
 * locked or not and change/reset the passwords.
 *
 */

public interface UserService{
  
	/**
	 * Creates the user
	 * 
	 * @param dto
	 *            UserModel
	 * @return User If user account exists
	 * @throws UserExistsException
	 *             if user account doesn't exists
	 */
	User createUser(UserModel dto) throws UserExistsException;

	/**
	 * Returns the User by user name.
	 * 
	 * @param username
	 *            String
	 */
	User getUserByUsername(String username);

	/**
	 * Changes the password.
	 * 
	 * @param user
	 *            ChangePasswordModel, token String
	 * @return Boolean
	 * 
	 * @throws InvalidTokenException
	 */
	Boolean changePassword(ChangePasswordModel user, String token) throws InvalidTokenException;

	/**
	 * Saves the login info to the repository.
	 * 
	 * @param login
	 *            UserLogin
	 * @throws UserLogin
	 */
	UserLogin recordLogin(UserLogin login);

	/**
	 * Verifies the account is locked or not.
	 * 
	 * @param username
	 *            String
	 * @return Boolean
	 */
	Boolean isAccountLocked(String username);

	/**
	 * Saves the user , password and ContactOptIn.
	 * 
	 * @param user
	 *            User, dto UserModel
	 * 
	 * @return User
	 * 
	 */
	User saveUser(User user);

	/**
	 * Saves the change password.
	 * 
	 * @param user
	 *            User, dto ChangePasswordModel
	 * 
	 * @return User
	 * 
	 */
	String createPasswordResetToken(String username) throws UserNotFoundException;

	/**
	 * Returns the Autorities based on user.
	 * 
	 * @param user
	 *            User
	 */
	Set<Authority> getAuthorities(User user);

	/**
	 * Changes the password.
	 * 
	 * @param dto
	 *            UserModel
	 * @return Boolean
	 * 
	 * @throws UserNotFoundException
	 */
	Boolean changePassword(UserModel user) throws UserNotFoundException;

}
