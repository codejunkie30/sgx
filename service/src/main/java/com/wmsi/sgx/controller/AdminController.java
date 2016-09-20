package com.wmsi.sgx.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.config.AppConfig.TrialProperty;
import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.account.AdminResponse;
import com.wmsi.sgx.model.account.TrialResponse;
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TokenHandler;
import com.wmsi.sgx.service.PropertiesService;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.AdminService;
import com.wmsi.sgx.service.account.UserExistsException;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	AdminService adminService;
	@Autowired
	private AccountService accountService;
	
	@Autowired 
	private PropertiesService propertiesService;
	
	@Autowired
	private TrialProperty getTrial;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;	
	
	@RequestMapping(value = "info", method = RequestMethod.POST)
	public @ResponseBody AccountModel account(HttpServletRequest request) throws UserExistsException{		
		return accountService.getAccountForUsername(findUserFromToken(request).getUsername());
	}
	
	@RequestMapping(value = "transId", method = RequestMethod.POST)
	public @ResponseBody AdminResponse enetstransId(HttpServletRequest HttpRequest, @RequestBody AdminResponse request) {	
		AccountModel acct = accountService.getAccountForUsername(findUserFromToken(HttpRequest).getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		AdminResponse response = new AdminResponse();
		User u = accountService.findUserForTransactionId(request.getTransId());
		if(u != null){
			AccountModel acc = accountService.getAccountForUsername(u.getUsername());
			response.setData(adminService.convertAccountModelToAdminAccountModel(acc));
			response.setResponseCode(0);
		}else{
			response.setData("Invalid transaction id");
			response.setResponseCode(41);
		}
		
		return response;
	}
	
	@RequestMapping(value="getTrial", method = RequestMethod.POST)
	public AdminResponse getTrial(HttpServletRequest request, @RequestBody TrialResponse response){
		AccountModel acct = accountService.getAccountForUsername(findUserFromToken(request).getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.getTrialDays();
	}
	
	@RequestMapping(value= "setTrial", method = RequestMethod.POST)
	public AdminResponse setTrial(HttpServletRequest request, @RequestBody TrialResponse response){		
		AccountModel acct = accountService.getAccountForUsername(findUserFromToken(request).getUsername());
		
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN )
			return isAdmin();
		return adminService.trialDay(response,acct.getEmail());
	}
	
	@RequestMapping(value = "findUser", method = RequestMethod.POST)
	public AdminResponse findUser(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		AccountModel acct = accountService.getAccountForUsername(findUserFromToken(request).getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		
		return adminService.findByUser(response.getId());
	}
	
	@RequestMapping(value = "searchDate", method = RequestMethod.POST)
	public AdminResponse searchByDate(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		AccountModel acct = accountService.getAccountForUsername(findUserFromToken(request).getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.searchByDate(response.getDateParam());
		
	}
	
	@RequestMapping(value = "deactivate", method = RequestMethod.POST)
	public AdminResponse deactivate(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		User user = findUserFromToken(request);
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.deactivate(response.getId(), user.getId());
		
	}
	
	@RequestMapping(value = "extension", method = RequestMethod.POST)
	public AdminResponse extension(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		User user = findUserFromToken(request);
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.extension(response.getId(), response.getDateParam(), user.getId());
		
	}
	
	@RequestMapping(value = "setAdmin", method = RequestMethod.POST)
	public AdminResponse setAdmin(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		User user = findUserFromToken(request);
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER)
			return isAdmin();
		if(acct.getType() == AccountType.MASTER){
			return adminService.setAdmin(response.getId(), user.getId());
		}
		
		return response;
		
	}
	
	@RequestMapping(value = "removeAdmin", method = RequestMethod.POST)
	public AdminResponse removeAdmin(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		User user = findUserFromToken(request);
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER)
			return isAdmin();
		if(acct.getType() == AccountType.MASTER){
			return adminService.removeAdmin(response.getId(), user.getId());
		}
		
		return response;
		
	}	
	
	public AdminResponse isAdmin(){
		AdminResponse ret = new AdminResponse();
		ret.setResponseCode(22);
		ret.setData("Account is not authorized.");
		return ret;
	}
	//Email / Status / Last Login / Last Payment / Trial Start / Trial Exp / Opted / Premium Start / Premium Expiration	
	@RequestMapping(value = "excel", produces = "text/csv;charset=utf-8")
	public void excel(@RequestParam("at") String token,HttpServletRequest request, HttpServletResponse response) throws IOException{
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		User user = null;
		if(token != null)
			user = tokenHandler.parseUserFromToken(token);
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() == AccountType.MASTER || acct.getType() == AccountType.ADMIN)
			adminService.writeCsv(response, new String[] { "Email Address", "Status", "Last Login", "Last Payment", "Trial Start", "Trial Expiration", "Email Opt In", "Premium Start", "Premium Expiration" }, "admin-excel-");
	}
	
	public User findUserFromToken(HttpServletRequest request){
		String token = request.getHeader("X-AUTH-TOKEN");
		
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		User user = null;
		if(token != null)
		 return user = tokenHandler.parseUserFromToken(token);
		return null;
	}
	
	
	
}
