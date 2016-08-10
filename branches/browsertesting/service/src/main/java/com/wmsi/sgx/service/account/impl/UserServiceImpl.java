package com.wmsi.sgx.service.account.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.Authority;
import com.wmsi.sgx.domain.PasswordReset;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.UserLogin;
import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.repository.PasswordResetRepository;
import com.wmsi.sgx.repository.UserLoginRepository;
import com.wmsi.sgx.repository.UserRepository;
import com.wmsi.sgx.repository.UserVerificationRepository;
import com.wmsi.sgx.security.SecureTokenGenerator;
import com.wmsi.sgx.service.account.InvalidTokenException;
import com.wmsi.sgx.service.account.UserExistsException;
import com.wmsi.sgx.service.account.UserNotFoundException;
import com.wmsi.sgx.service.account.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userReposistory;
	
	@Autowired
	private AccountRepository accountReposistory;

	@Autowired
	private UserVerificationRepository userVerificationReposistory;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private SecureTokenGenerator tokenGenerator;
	
	@Autowired
	private UserLoginRepository userLoginRepository;
	
	@Override
	@Transactional
	public User createUser(UserModel dto) throws UserExistsException{

		if(getUserByUsername(dto.getEmail()) != null)
			throw new UserExistsException("There is already an account with this email address");
			
		// Save user
		User user = saveUser(new User(), dto);
		
		return user;
	}

	@Override
	public Set<Authority> getAuthorities(User user){
		return user.getAuthorities();
	}
	
	@Override
	public User getUserByUsername(String username){		
		return userReposistory.findByUsername(username);
	}
	
	@Override
	@Transactional
	public User saveUser(User user){		
		return userReposistory.save(user);
	}

	private User saveUser(User user, UserModel dto){

		user.setUsername(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setContactOptIn(dto.getContactOptIn());
		
		return saveUser(user);
	}
	
	private User saveChangePasswordUser(User user, ChangePasswordModel dto){

		user.setUsername(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		
		return userReposistory.save(user);
	}

	@Autowired
	private PasswordResetRepository passwordResetRepository;
	
	@Override
	@Transactional
	public String createPasswordResetToken(String username) throws UserNotFoundException{
		
		User user = getUserByUsername(username);
		
		if(user == null)
			throw new UserNotFoundException();
			
		PasswordReset reset = new PasswordReset();
		reset.setUser(user);
		reset.setToken(tokenGenerator.nextToken());
		
		reset = passwordResetRepository.save(reset);
		
		return reset.getToken();
	}
	@Value ("${password.reset.timer}")
	private int PASSWORD_TTL_SECS;
	
	@Override
	@Transactional
	public Boolean changePassword(ChangePasswordModel user, String token) throws InvalidTokenException {
		
		PasswordReset reset = passwordResetRepository.findByToken(token);
		
		if(reset == null)
			throw new InvalidTokenException("Token not found");
				
		// Check if token has been used or expired.		
		DateTime creationDate = new DateTime(reset.getDate());
		
		if(reset.getRedeemed() || creationDate.plusSeconds(PASSWORD_TTL_SECS).isBeforeNow())
			throw new InvalidTokenException("Password token has expired");
		
		User resetUser = reset.getUser();
		
		// Make sure token matches the user name
		if(!resetUser.getUsername().equals(user.getEmail()))
			throw new InvalidTokenException("User does not match token");
		
		// Update password
		saveChangePasswordUser(resetUser, user);
		
		// Set redemption flag to prevent this token from being used twice
		reset.setRedeemed(true);
		passwordResetRepository.save(reset);
		
		return true;
	}

	@Override
	@Transactional
	//@PreAuthorize("hasRole('ROLE_USER')")
	public Boolean changePassword(UserModel dto) throws UserNotFoundException{
		
		User user = getUserByUsername(dto.getEmail());
		
		// Make sure token matches the user name
		if(user == null)
			throw new UserNotFoundException();
		
		// Update password
		saveUser(user, dto);
		
		return true;
	}

	@Override
	@Transactional
	public UserLogin recordLogin(UserLogin login){
		return userLoginRepository.save(login);		
	}
	
	private static final int LOCKOUT_TIME = 30 * 60; // Thirty minutes
	private static final int LOCKOUT_LIMIT = 3; // Three Attempts
	
	@Override
	public Boolean isAccountLocked(String username){

		List<UserLogin> logins =  userLoginRepository
							.findByUsernameAndDateGreaterThanOrderByDateDesc(
									username, 
									new DateTime().minusSeconds(LOCKOUT_TIME).toDate());
		
		int failed = 0;
		
		// Count all failed logins since last successful
		for(UserLogin login : logins){
			
			// If we find a successful login we can stop
			if(login.getSuccess() || failed >= LOCKOUT_LIMIT)
				break;
			
			failed++;
		}
		
		return failed >= LOCKOUT_LIMIT;
	}

	
}