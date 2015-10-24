package com.wmsi.sgx.service.account.impl;

import java.text.MessageFormat;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.wmsi.sgx.domain.Role;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.ResetUser;
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
import com.wmsi.sgx.service.account.VerifiedUserException;

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
	public void registerUser(UserModel dto) throws UserExistsException, MessagingException{
	
		// Create the user record in the database.
		User user = userService.createUser(dto);

		// Add default user role
		user.addRole(Role.ROLE_USER);
		
		String token = userVerificationService.createVerificationToken(user);
		
		sendVerificationEmail(user.getUsername(), token);
	}
	
	@Override
	@Transactional
	public void resendVerificationEmail(String username) throws MessagingException{

		User user = userService.getUserByUsername(username);
		
		// TODO Check for existing verification records to prevent someone spamming user by
		// requesting resets over and over.
		String token = userVerificationService.createVerificationToken(user);
		
		sendVerificationEmail(username, token);
	}
	
	@Override
	@Transactional
	public Boolean verifyUser(String token) throws UserVerificationException, AccountCreationException, VerifiedUserException{
	
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
		
		
		return accountService.convertToExpiry(user);
	}
	
	@Override
	@Transactional
	public Boolean convertToExpiredAccount(String email) throws UserNotFoundException{

		User user = userService.getUserByUsername(email);
		
		// Remove trial access
		if(user.hasRole(Role.ROLE_TRIAL))
			user.removeRole(Role.ROLE_TRIAL);
				
		// Add expired role
		user.addRole(Role.ROLE_EXPIRED);
		
		userService.saveUser(user);
		
		// Create premium account record
		accountService.convertToExpiry(user);
		
		return true;
	}
	
	@Override
	@Transactional
	public Boolean sendPasswordReset(String email) throws UserNotFoundException, MessagingException{
		
		String token = userService.createPasswordResetToken(email);
		
		Boolean created = !StringUtils.isEmpty(token);
		
		if(created)
			sendResetEmail(email, token);
		
		return created;
	}

	@Override
	@Transactional
	public Boolean resetPassword(ChangePasswordModel user, String resetToken) throws InvalidTokenException, MessagingException{
		
		Boolean success = userService.changePassword(user, resetToken);
		
		if(success)
			sendResetConfirmationEmail(user.getEmail());
		
		return success;
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_USER') and authentication.name == #user.email")
	public Boolean changePassword(UserModel user) throws UserNotFoundException, MessagingException{
		
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
	
	@Value ("${email.verify.email}")
	private String emailBody;
	@Value ("${email.reset.password}")
	private String resetEmailBody;	
	@Value ("${email.reset.confirm}")
	private String resetConfirmEmailBody;

	private void sendVerificationEmail(String email, String token) throws MessagingException{
		emailService.send(email, "SGX StockFacts Premium: Verify Your Email Address", token, emailBody);
	}
	
	private void sendResetEmail(String email, String token) throws MessagingException{
		emailService.send(email, "SGX StockFacts Premium: Reset Your Password", token, resetEmailBody);
	}

	private void sendResetConfirmationEmail(String email) throws MessagingException{
		emailService.send(email, "SGX StockFacts Premium: Password Reset", null, resetConfirmEmailBody);
	}


}
