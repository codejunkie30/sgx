
package com.wmsi.sgx.controller;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wmsi.sgx.domain.CustomAuditorAware;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.ApiResponse;
import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.ResetUser;
import com.wmsi.sgx.model.VerifyUser;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.service.RSAKeyException;
import com.wmsi.sgx.service.RSAKeyService;
import com.wmsi.sgx.service.account.AccountCreationException;
import com.wmsi.sgx.service.account.InvalidTokenException;
import com.wmsi.sgx.service.account.RegistrationService;
import com.wmsi.sgx.service.account.UserExistsException;
import com.wmsi.sgx.service.account.UserNotFoundException;
import com.wmsi.sgx.service.account.UserVerificationException;
import com.wmsi.sgx.service.account.VerifiedUserException;
import com.wmsi.sgx.service.account.impl.CreateUserReponse;

/**
 * This controller is used for performing various actions related to the user.
 *
 */
@Controller
@RequestMapping("/user")
public class UserController{

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private RSAKeyService rsaKeyService;
	
	@Autowired
	private LocalValidatorFactoryBean validator;
	
	@Autowired
	private CustomAuditorAware<User> auditorProvider;

	/**
	 * Registers the user and sends the mail after registration.
	 * 
	 * @param user
	 *            UserModel
	 * @param result
	 *            BindingResult
	 * @return Boolean
	 * @return
	 * @throws UserExistsException
	 * @throws MessagingException
	 * @throws RSAKeyException
	 * @throws MethodArgumentNotValidException
	 */
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public @ResponseBody Boolean register(@RequestBody UserModel user, BindingResult result) throws UserExistsException, MessagingException, RSAKeyException, MethodArgumentNotValidException{
		decryptUserModelParams(user);
		validator.validate(user, result);
		if (result.hasErrors()) {
			throw new MethodArgumentNotValidException(null, result);
		}
		User auditUser = new User();
		auditUser.setId(1L);
		auditorProvider.setUser(auditUser);
		CreateUserReponse res = registrationService.registerUser(user);
		try {
			registrationService.sendVerificationEmail(res.getUsername(), res.getToken());
		} catch (Exception e) {
			log.error("Exception occrued in sending email", e);
		}
		return true;
	}
	
	/**
	 * Verifies the validity of the token that is assigned to the user.
	 * 
	 * @param user
	 *            VerifyUser
	 * @return Boolean
	 * 
	 * @throws UserVerificationException
	 * @throws AccountCreationException
	 * @throws VerifiedUserException
	 */
	@RequestMapping(value = "verify", method = RequestMethod.POST)
	public @ResponseBody Boolean verify(@RequestBody VerifyUser user) throws UserVerificationException, AccountCreationException, VerifiedUserException{
	
		return registrationService.verifyUser(user.getToken());
	}
	
	/**
	 * Resets the token that is assigned to the user.
	 * 
	 * @param user
	 *            ResetUser
	 * @return ApiResponse
	 * 
	 * @throws UserNotFoundException
	 * @throws MessagingException
	 * @throws RSAKeyException
	 */
	@RequestMapping(value = "resetToken", method = RequestMethod.POST)
	public @ResponseBody ApiResponse resetToken(@RequestBody ResetUser user) throws UserNotFoundException, MessagingException, RSAKeyException{
		decryptUsername(user);
		return registrationService.resendVerificationEmail(user.getUsername());
	}

	/**
	 * Resets the password of the user.
	 * 
	 * @param user
	 *            ResetUser
	 * @return Boolean
	 * 
	 * @throws UserNotFoundException
	 * @throws RSAKeyException
	 */
	@RequestMapping(value = "reset", method = RequestMethod.POST)
	public @ResponseBody Boolean reset(@RequestBody ResetUser user) throws UserNotFoundException, RSAKeyException{
	
		decryptUsername(user);
		try{
			return registrationService.sendPasswordReset(user.getUsername());	
			
		}
		catch(UserNotFoundException ue){
			log.debug("Password reset user not found", ue );
		}
		catch(Exception e){
			log.debug("Exception occrued in password reset", e);			
		}
		
		return true;
		
	}

	/**
	 * Expires the user explicitly.
	 * 
	 * @param user
	 *            ResetUser
	 * @return Boolean
	 * 
	 * @throws UserNotFoundException
	 * @throws RSAKeyException
	 */
	//Strictly used for internal testing, 
	//you can make a user expired by calling this api endpoint  
	@RequestMapping(value = "expireUser", method = RequestMethod.POST)
	public @ResponseBody Boolean expireUser(@RequestBody ResetUser user) throws UserNotFoundException, RSAKeyException{
	
		try{
			registrationService.convertToExpiredAccount(user.getUsername());	
		}
		catch(UserNotFoundException ue){
			log.debug("Expire User not found", ue );
		}
		catch(Exception e){
			log.debug("Exception occrued expire User Call", e);			
		}
		
		return true;
		
	}
	
	/**
	 * Changes the password of the user.
	 * 
	 * @param token
	 * @param user
	 * @param result
	 * @return
	 * @throws InvalidTokenException
	 * @throws MessagingException
	 * @throws RSAKeyException
	 * @throws MethodArgumentNotValidException
	 */
	@RequestMapping(value = "password", method = RequestMethod.POST)
	public @ResponseBody Boolean changePassword(@RequestParam("ref") String token, @RequestBody ChangePasswordModel user, BindingResult result) throws InvalidTokenException, MessagingException, RSAKeyException, MethodArgumentNotValidException{
		//For auditing purpose
		User auditUser = new User();
		auditUser.setId(1L);
		auditorProvider.setUser(auditUser);
		
		decryptChangePasswordModel(user);
		validator.validate(user, result);
		if (result.hasErrors()) {
			throw new MethodArgumentNotValidException(null,result);
		}
		return registrationService.resetPassword(user, token);
	}
	
	/**
	 * Registers the premium user.
	 * 
	 * @param user
	 *            UserModel
	 * @return Boolean
	 * @throws RSAKeyException
	 */
	@RequestMapping(value = "premium", method = RequestMethod.POST)
	public @ResponseBody Boolean registerPremium(@RequestBody UserModel user) throws RSAKeyException{
		
		// TODO Ecomm integration 
		// This end point should not be public but is here for testing purposes
		// remove this and integrate this service call with the Ecommerce callback for successful payment.
		return registrationService.convertToPremiumAccount(user);
	}
	
	/**
	 * Decrypts the user info.
	 * 
	 * @param user
	 *            ResetUser
	 * @throws RSAKeyException
	 */
	private void decryptUsername(ResetUser user) throws RSAKeyException {
		try {
			user.setUsername(rsaKeyService.decrypt(user.getUsername()));
		} catch (RSAKeyException e) {
			log.error("Error in decrypting the username",e);
			throw new RSAKeyException("username is not valid");
		}
	}

	/**
	 * Decrypts the {@link ChangePasswordModel}
	 * 
	 * @param user
	 *            ChangePasswordModel
	 * @throws RSAKeyException
	 */
	private void decryptChangePasswordModel(ChangePasswordModel user) throws RSAKeyException {
		try {
			user.setEmail(rsaKeyService.decrypt(user.getEmail()));
			user.setPassword(rsaKeyService.decrypt(user.getPassword()));
			user.setPasswordMatch(rsaKeyService.decrypt(user.getPasswordMatch()));
		} catch (RSAKeyException e) {
			log.error("Error in decrypting the ChangePasswordModel",e);
			throw new RSAKeyException("Email, password or passwordmatch is not valid");
		}
	}

	/**
	 * Decrypts the {@link UserModel}
	 * 
	 * @param user
	 *            UserModel
	 * @throws RSAKeyException
	 */
	private void decryptUserModelParams(UserModel user) throws RSAKeyException {
		try {
			user.setEmail(rsaKeyService.decrypt(user.getEmail()));
			user.setPassword(rsaKeyService.decrypt(user.getPassword()));
			user.setPasswordMatch(rsaKeyService.decrypt(user.getPasswordMatch()));
		} catch (RSAKeyException e) {
			log.error("Error in decrypting the UserModel",e);
			throw new RSAKeyException("Email, password or passwordmatch is not valid");
		}
	}
}
