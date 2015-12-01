
package com.wmsi.sgx.controller;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wmsi.sgx.model.ApiResponse;
import com.wmsi.sgx.model.ChangePasswordModel;
import com.wmsi.sgx.model.ResetUser;
import com.wmsi.sgx.model.VerifyUser;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.service.account.AccountCreationException;
import com.wmsi.sgx.service.account.InvalidTokenException;
import com.wmsi.sgx.service.account.RegistrationService;
import com.wmsi.sgx.service.account.UserExistsException;
import com.wmsi.sgx.service.account.UserNotFoundException;
import com.wmsi.sgx.service.account.UserVerificationException;
import com.wmsi.sgx.service.account.VerifiedUserException;
import com.wmsi.sgx.service.account.impl.CreateUserReponse;

@Controller
@RequestMapping("/user")
public class UserController{

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private RegistrationService registrationService;

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public @ResponseBody Boolean register(@Valid @RequestBody UserModel user) throws UserExistsException, MessagingException{
		
		CreateUserReponse res =registrationService.registerUser(user);
		try{
		registrationService.sendVerificationEmail(res.getUsername(),res.getToken());
		}
		catch(Exception e){
			log.debug("Exception occrued in sending email", e);			
		}
		return true;
	}
	
	@RequestMapping(value = "verify", method = RequestMethod.POST)
	public @ResponseBody Boolean verify(@RequestBody VerifyUser user) throws UserVerificationException, AccountCreationException, VerifiedUserException{
	
		return registrationService.verifyUser(user.getToken());
	}
	
	@RequestMapping(value = "resetToken", method = RequestMethod.POST)
	public @ResponseBody ApiResponse resetToken(@RequestBody ResetUser user) throws UserNotFoundException, MessagingException{
		
		return registrationService.resendVerificationEmail(user.getUsername());
	}

	@RequestMapping(value = "reset", method = RequestMethod.POST)
	public @ResponseBody Boolean reset(@RequestBody ResetUser user) throws UserNotFoundException{
	
		 
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
	//Strictly used for internal testing, 
	//you can make a user expired by calling this api endpoint  
	@RequestMapping(value = "expireUser", method = RequestMethod.POST)
	public @ResponseBody Boolean expireUser(@RequestBody ResetUser user) throws UserNotFoundException{
	
		
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

	@RequestMapping(value = "password", method = RequestMethod.POST)
	public @ResponseBody Boolean changePassword(@RequestParam("ref") String token, @Valid @RequestBody ChangePasswordModel user) throws InvalidTokenException, MessagingException{
		return registrationService.resetPassword(user, token);
	}
	
	// TEMP TEMP TEMP TEMP
	// The following endpoint is for proof of concept only and should be removed once ecommerce is full
	// integrated. 
	@RequestMapping(value = "premium", method = RequestMethod.POST)
	public @ResponseBody Boolean registerPremium(@RequestBody UserModel user){
		
		
		// TODO Ecomm integration 
		// This end point should not be public but is here for testing purposes
		// remove this and integrate this service call with the Ecommerce callback for successful payment.
		return registrationService.convertToPremiumAccount(user);
	}

}
