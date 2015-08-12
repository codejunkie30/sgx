package com.wmsi.sgx.service.account.impl;

import java.text.MessageFormat;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.wmsi.sgx.domain.Role;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.service.EmailService;
import com.wmsi.sgx.service.account.AccountCreationException;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.InvalidTokenException;
import com.wmsi.sgx.service.account.RegistrationService;
import com.wmsi.sgx.service.account.UserExistsException;
import com.wmsi.sgx.service.account.UserNotFoundException;
import com.wmsi.sgx.service.account.UserService;
import com.wmsi.sgx.service.account.UserVerificationException;
import com.wmsi.sgx.service.account.UserVerificationService;

@Service
public class RegistrationServiceImpl implements RegistrationService{

	@Autowired
	private UserService userService;

	@Autowired
	private UserVerificationService userVerificationService;

	@Autowired
	private AccountService accountService;
	
	@Override
	@Transactional
	public void registerUser(UserModel dto) throws UserExistsException{
	
		// Create the user record in the database.
		User user = userService.createUser(dto);

		// Add default user role
		user.addRole(Role.ROLE_USER);
		
		String token = userVerificationService.createVerificationToken(user);
		
		sendVerificationEmail(user.getUsername(), token);
	}
	
	@Override
	@Transactional
	public void resendVerificationEmail(String username){

		User user = userService.getUserByUsername(username);
		
		// TODO Check for existing verification records to prevent someone spamming user by
		// requesting resets over and over.
		String token = userVerificationService.createVerificationToken(user);
		
		sendVerificationEmail(username, token);
	}
	
	@Override
	@Transactional
	public Boolean verifyUser(String token) throws UserVerificationException, AccountCreationException{
	
		User user = userVerificationService.verifyToken(token);
		
		// Create trial account record
		accountService.createTrialAccount(user);
		
		// Add Trial access
		user.addRole(Role.ROLE_TRIAL);
		
		// Enable user and grant authorities
		user.setEnabled(true);		
		userService.saveUser(user);

		return true;		
	}
	
	@Override
	@Transactional
	public Boolean convertToPremiumAccount(UserModel dto){

		// TODO ECommerce validation to make sure user has paid.

		User user = userService.getUserByUsername(dto.getEmail());
		
		// Remove trial access
		if(user.hasRole(Role.ROLE_TRIAL))
			user.removeRole(Role.ROLE_TRIAL);
				
		// Add premium access
		user.addRole(Role.ROLE_PREMIUM);
		
		userService.saveUser(user);
		
		// Create premium account record
		accountService.createPremiumAccount(user);
		
		return true;
	}
	
	@Override
	@Transactional
	public Boolean sendPasswordReset(String email) throws UserNotFoundException{
		
		String token = userService.createPasswordResetToken(email);
		
		Boolean created = !StringUtils.isEmpty(token);
		
		if(created)
			sendResetEmail(email, token);
		
		return created;
	}

	@Override
	@Transactional
	public Boolean resetPassword(UserModel user, String resetToken) throws InvalidTokenException{
		
		Boolean success = userService.changePassword(user, resetToken);
		
		if(success)
			sendResetConfirmationEmail(user.getEmail());
		
		return success;
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_USER') and authentication.name == #user.email")
	public Boolean changePassword(UserModel user) throws UserNotFoundException{
		
		Boolean success = userService.changePassword(user);
		
		if(success)
			sendResetConfirmationEmail(user.getEmail());
		
		return success;
	}

	/****************
	// TODO Replace this with some kind of templating engine so UI devs can maintain email body.
	/*********************/
	
	@Autowired
	private EmailService emailService;
	
	private static final String emailBody = "Please verify your email address http://localhost:8080/sgx/user/verify?ref={0}";	
	private static final String resetEmailBody = "To reset your password vist http://localhost:8080/sgx/user/password?ref={0}";	
	private static final String resetConfirmEmailBody = "Your password was reset.";

	private void sendVerificationEmail(String email, String token){
		emailService.send(email, "Verify Email", MessageFormat.format(emailBody, token));
	}
	
	private void sendResetEmail(String email, String token){
		emailService.send(email, "Reset Password", MessageFormat.format(resetEmailBody, token));
	}

	private void sendResetConfirmationEmail(String email){
		emailService.send(email, "Password Reset", resetConfirmEmailBody);
	}


}
