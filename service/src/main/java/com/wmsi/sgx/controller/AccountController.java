package com.wmsi.sgx.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.dto.AccountModel;
import com.wmsi.sgx.dto.PasswordChange;
import com.wmsi.sgx.dto.UserDTO;
import com.wmsi.sgx.security.UserDetailsWrapper;
import com.wmsi.sgx.service.AccountService;
import com.wmsi.sgx.service.RegistrationService;
import com.wmsi.sgx.service.UserExistsException;
import com.wmsi.sgx.service.UserNotFoundException;

@RestController
@RequestMapping("/account")
public class AccountController{

	@Autowired
	private AccountService accountService;
	
	@Autowired 
	private RegistrationService registrationService;
	
	@RequestMapping(value = "info", method = RequestMethod.GET)
	public @ResponseBody AccountModel account(@AuthenticationPrincipal UserDetailsWrapper user) throws UserExistsException{
		
		return accountService.getAccountForUsername(user.getUsername());
	}

	@RequestMapping(value = "password", method = RequestMethod.POST)
	public @ResponseBody Boolean changePassword(@AuthenticationPrincipal UserDetailsWrapper user, @Valid PasswordChange pass) throws UserNotFoundException{
	
		String username = user.getUsername();
		
		UserDTO dto = new UserDTO();
		dto.setEmail(username);
		dto.setPassword(pass.getPassword());
		dto.setPasswordMatch(pass.getPasswordMatch());
		
		return registrationService.changePassword(dto);
	}

	
}
