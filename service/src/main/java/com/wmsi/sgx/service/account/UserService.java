package com.wmsi.sgx.service.account;

import java.util.Set;

import org.springframework.security.authentication.AccountExpiredException;

import com.wmsi.sgx.domain.Authority;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.UserLogin;
import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.account.UserModel;

public interface UserService{
	User createUser(UserModel dto) throws UserExistsException;

	User getUserByUsername(String username);

	Boolean changePassword(ChangePasswordModel user, String token) throws InvalidTokenException;

	UserLogin recordLogin(UserLogin login);

	Boolean isAccountLocked(String username);
	
	Boolean isAccountExpired(String username);

	User saveUser(User user);

	String createPasswordResetToken(String username) throws UserNotFoundException;

	Set<Authority> getAuthorities(User user);

	Boolean changePassword(UserModel user) throws UserNotFoundException;

}
