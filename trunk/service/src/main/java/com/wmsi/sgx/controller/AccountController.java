package com.wmsi.sgx.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.model.UpdateAccountModel;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.account.PasswordChangeModel;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.security.UserDetailsWrapper;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.RegistrationService;
import com.wmsi.sgx.service.account.UserExistsException;
import com.wmsi.sgx.service.account.UserNotFoundException;

@RestController
@RequestMapping("/account")
public class AccountController{

	@Autowired
	private AccountService accountService;
	
	@Autowired 
	private RegistrationService registrationService;
	
	@RequestMapping(value = "info", method = RequestMethod.POST)
	public @ResponseBody AccountModel account(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody UpdateAccountModel dto) throws UserExistsException{
		
		return accountService.getAccountForUsername(user.getUsername());
	}

	@RequestMapping(value = "password", method = RequestMethod.POST)
	public @ResponseBody Boolean changePassword(@AuthenticationPrincipal UserDetailsWrapper user, @Valid @RequestBody PasswordChangeModel pass) throws UserNotFoundException{
	
		String username = user.getUsername();
		
		UserModel dto = new UserModel();
		dto.setEmail(username);
		dto.setPassword(pass.getPassword());
		dto.setPasswordMatch(pass.getPasswordMatch());
		
		return registrationService.changePassword(dto);
	}
	
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public @ResponseBody AccountModel updateAccount(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody UpdateAccountModel dto) throws UserNotFoundException{
		
		String username = user.getUsername();
		dto.setEmail(username);
		return accountService.updateAccount(dto);
		
	}
	
}
