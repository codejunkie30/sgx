package com.wmsi.sgx.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wmsi.sgx.model.ResetUser;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.service.account.AccountCreationException;
import com.wmsi.sgx.service.account.InvalidTokenException;
import com.wmsi.sgx.service.account.RegistrationService;
import com.wmsi.sgx.service.account.UserExistsException;
import com.wmsi.sgx.service.account.UserNotFoundException;
import com.wmsi.sgx.service.account.UserVerificationException;

@Controller
@RequestMapping("/user")
public class UserController{

	@Autowired
	private RegistrationService registrationService;

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public @ResponseBody Boolean register(@Valid @RequestBody UserModel user) throws UserExistsException{
		
		registrationService.registerUser(user);

		return true;
	}
	
	@RequestMapping(value = "verify", method = RequestMethod.GET)
	public @ResponseBody Boolean verify(@RequestParam("ref") String token) throws UserVerificationException, AccountCreationException{
	
		return registrationService.verifyUser(token);
	}

	@RequestMapping(value = "reset", method = RequestMethod.POST)
	public @ResponseBody Boolean reset(@RequestBody ResetUser user) throws UserNotFoundException{
	
		return registrationService.sendPasswordReset(user.getUsername());
	}

	@RequestMapping(value = "password", method = RequestMethod.POST)
	public @ResponseBody Boolean changePassword(@RequestParam("ref") String token, @Valid @RequestBody UserModel user) throws InvalidTokenException{
	
		return registrationService.resetPassword(user, token);
	}
	
	// TEMP TEMP TEMP TEMP
	// The following endpoing is for proof of concept only and should be removed once ecommerce is full
	// intetgrated. 
	@RequestMapping(value = "premium", method = RequestMethod.POST)
	public @ResponseBody Boolean registerPremium(@RequestBody UserModel user){
		
		
		// TODO Ecomm integeration 
		// This end point should not be public but is here for testing purposes
		// remove this and integrate this service call with the Ecommerce callback for successful payment.
		return registrationService.convertToPremiumAccount(user);
	}

}
