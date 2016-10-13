package com.wmsi.sgx.service.account;

import java.util.Set;

import org.springframework.security.authentication.AccountExpiredException;

import com.wmsi.sgx.domain.Authority;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.UserLogin;
import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.account.UserModel;

/**
 * The UserService handles operations related to User
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
	 *            ChangePasswordModel
	 * @param token
	 *            String
	 * @return Returns true if the password change succeeded otherwise false
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
	 * Saves the user, password and ContactOptIn.
	 * 
	 * @param user User
	 * @return User
	 */
	User saveUser(User user);

	/**
	 * Creates the password reset token
	 * 
	 * @param username
	 *            String
	 * @return String Password reset token
	 * @throws UserNotFoundException
	 */
	String createPasswordResetToken(String username) throws UserNotFoundException;

	/**
	 * Returns the Authorities based on user.
	 * 
	 * @param user User
	 * @return Authority information
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
