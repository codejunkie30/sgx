package com.wmsi.sgx.service.account.impl;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.wmsi.sgx.domain.CustomAuditorAware;
import com.wmsi.sgx.domain.Role;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.ApiResponse;
import com.wmsi.sgx.model.ChangePasswordModel;
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

/**
 * The RegistrationServiceImpl class handles operations like user registration,
 * sending the verification email, verifying the user and password change.
 *
 */
@Service
public class RegistrationServiceImpl implements RegistrationService{

	@Autowired
	private UserService userService;

	@Autowired
	private UserVerificationService userVerificationService;

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private MessageSource messages;
	
	@Autowired
	private CustomAuditorAware<User> auditorProvider;
	
	/**
	 * Registers the user.
	 * 
	 * @param dto UserModel
	 * @return CreateUserReponse
	 * @throws UserExistsException
	 * @throws MessagingException
	 */
	@Override
	@Transactional
	public CreateUserReponse registerUser(UserModel dto) throws UserExistsException, MessagingException{
		CreateUserReponse res = new CreateUserReponse();
		// Create the user record in the database.
		User user = userService.createUser(dto);

		// Add default user role
		user.addRole(Role.ROLE_USER);
		
		String token = userVerificationService.createVerificationToken(user);
		res.setToken(token);
		res.setUsername(user.getUsername());
		//sendVerificationEmail(user.getUsername(), token);
		return res;
	}
	
	/**
	 * Resends the verification mail.
	 * 
	 * @param username
	 * @return ApiResponse
	 * @throws MessagingException
	 */
	@Override
	@Transactional
	public ApiResponse resendVerificationEmail(String username) throws MessagingException{

		ApiResponse res = new ApiResponse();
		User user = userService.getUserByUsername(username);
		if(user==null){
			res.setMessage(messages.getMessage("user.doesnot.exist",null,LocaleContextHolder.getLocale()));
			res.setMessageCode(messages.getMessage("user.doesnot.exist.code",null,LocaleContextHolder.getLocale()));
			return res;
		}else{
		
			if(user!=null && user.getEnabled()==false){
			String token = userVerificationService.createVerificationToken(user);
			
			sendVerificationEmail(username, token);
			res.setMessage(messages.getMessage("success",null,LocaleContextHolder.getLocale()));
			res.setMessageCode(messages.getMessage("success.code",null,LocaleContextHolder.getLocale()));
			return res;
				}else{
				res.setMessage(messages.getMessage("user.already.verified",null,LocaleContextHolder.getLocale()));
				res.setMessageCode(messages.getMessage("user.already.verified.code",null,LocaleContextHolder.getLocale()));
				return res;
				}
		}	
	}
	
	/**
	 * Verifies the user.
	 * 
	 * @param token
	 * @return true if the user verification is succeeded otherwise false
	 * @throws UserVerificationException
	 * @throws AccountCreationException
	 * @throws VerifiedUserException
	 */
	@Override
	@Transactional
	public Boolean verifyUser(String token) throws UserVerificationException, AccountCreationException, VerifiedUserException{
	
		User user = userVerificationService.verifyToken(token);
		auditorProvider.setUser(user);
		// Create trial account record
		accountService.createTrialAccount(user);
		
		// Add Trial access
		user.addRole(Role.ROLE_TRIAL);
		
		// Enable user and grant authorities
		user.setEnabled(true);		
		userService.saveUser(user);

		return true;		
	}
	
	/**
	 * Converts the user to Premium user Account.
	 * 
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional
	public Boolean convertToPremiumAccount(UserModel dto){

		// TODO ECommerce validation to make sure user has paid.

		User user = userService.getUserByUsername(dto.getEmail());
		auditorProvider.setUser(user);
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
	
	/**
	 * Expires the account.
	 * 
	 * @param email
	 * @return Returns true if the expire account conversion is succeeded
	 *         otherwise false
	 * @throws UserNotFoundException
	 */
	@Override
	@Transactional
	public Boolean convertToExpiredAccount(String email) throws UserNotFoundException{

		User user = userService.getUserByUsername(email);
		auditorProvider.setUser(user);
		
		accountService.convertToExpiry(user);
		
		return true;
	}
	
	/**
	 * Sends email to reset the password.
	 * 
	 * @param email
	 * @return 
	 * @throws UserNotFoundException
	 * @throws MessagingException
	 */
	@Override
	@Transactional
	public Boolean sendPasswordReset(String email) throws UserNotFoundException, MessagingException{
		
		String token = userService.createPasswordResetToken(email);
		
		Boolean created = !StringUtils.isEmpty(token);
		
		if(created)
			sendResetEmail(email, token);
		
		return created;
	}
	
	/**
	 * Resets the password.
	 * 
	 * @param user
	 * @param resetToken
	 * @return true if the password is reset otherwise false
	 * @throws InvalidTokenException
	 * @throws MessagingException
	 */
	@Override
	@Transactional
	public Boolean resetPassword(ChangePasswordModel user, String resetToken) throws InvalidTokenException, MessagingException{
		
		Boolean success = userService.changePassword(user, resetToken);
		
		if(success)
			sendResetConfirmationEmail(user.getEmail());
		
		return success;
	}
	
	/**
	 * Changes the password.
	 * 
	 * @param user
	 * @return true if the user password is changed otherwise false
	 * @throws UserNotFoundException
	 * @throws MessagingException
	 */
	@Override
	@Transactional
	//@PreAuthorize("hasRole('ROLE_USER') and authentication.name == #user.email")
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
	
	/**
	 * Sends the verification mail to the user.
	 * 
	 * @param email
	 * @param token
	 * @throws MessagingException
	 */
	@Override
	public void sendVerificationEmail(String email, String token) throws MessagingException{
		emailService.send(email, "SGX StockFacts Plus: Verify Your Email Address", token, emailBody);
	}
	
	/**
	 * Sends the Password Reset mail to the user.
	 * 
	 * @param email
	 * @param token
	 * @throws MessagingException
	 */
	private void sendResetEmail(String email, String token) throws MessagingException{
		emailService.send(email, "SGX StockFacts Plus: Reset Your Password", token, resetEmailBody);
	}
	
	/**
	 * Sends the Password Reset confirmation mail to the user.
	 * 
	 * @param email
	 *            String
	 * 
	 * @throws MessagingException
	 */

	private void sendResetConfirmationEmail(String email) throws MessagingException{
		emailService.send(email, "SGX StockFacts Plus: Password Reset", null, resetConfirmEmailBody);
	}


}
