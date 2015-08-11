package com.wmsi.sgx.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wmsi.sgx.dto.UserDTO;
import com.wmsi.sgx.service.AccountCreationException;
import com.wmsi.sgx.service.InvalidTokenException;
import com.wmsi.sgx.service.RegistrationService;
import com.wmsi.sgx.service.UserExistsException;
import com.wmsi.sgx.service.UserNotFoundException;
import com.wmsi.sgx.service.UserVerificationException;

@Controller
@RequestMapping("/user")
public class UserController{

	@Autowired
	private RegistrationService registrationService;

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public @ResponseBody Boolean register(@Valid UserDTO user) throws UserExistsException{
		
		registrationService.registerUser(user);

		return true;
	}
	
	@RequestMapping(value = "verify", method = RequestMethod.GET)
	public @ResponseBody Boolean verify(@RequestParam("ref") String token) throws UserVerificationException, AccountCreationException{
	
		return registrationService.verifyUser(token);
	}

	@RequestMapping(value = "reset", method = RequestMethod.POST)
	public @ResponseBody Boolean reset(@RequestParam("username") String username) throws UserNotFoundException{
	
		return registrationService.sendPasswordReset(username);
	}

	@RequestMapping(value = "password", method = RequestMethod.POST)
	public @ResponseBody Boolean changePassword(@RequestParam("ref") String token, @Valid UserDTO user) throws InvalidTokenException{
	
		return registrationService.resetPassword(user, token);
	}
	
	@RequestMapping(value = "premium", method = RequestMethod.POST)
	public @ResponseBody Boolean registerPremium(UserDTO user){
		
		// TODO Ecomm integeration
		return registrationService.convertToPremiumAccount(user);
	}

}
