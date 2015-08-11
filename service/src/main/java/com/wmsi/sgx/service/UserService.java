package com.wmsi.sgx.service;

import java.util.Set;

import com.wmsi.sgx.domain.Authority;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.UserLogin;
import com.wmsi.sgx.dto.UserDTO;

public interface UserService{

	User createUser(UserDTO dto) throws UserExistsException;

	User getUserByUsername(String username);

	Boolean changePassword(UserDTO user, String token) throws InvalidTokenException;

	UserLogin recordLogin(UserLogin login);

	Boolean isAccountLocked(String username);

	User saveUser(User user);

	String createPasswordResetToken(String username) throws UserNotFoundException;

	Set<Authority> getAuthorities(User user);

	Boolean changePassword(UserDTO user) throws UserNotFoundException;

}
