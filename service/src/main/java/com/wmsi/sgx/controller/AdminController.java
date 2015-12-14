package com.wmsi.sgx.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wmsi.sgx.config.AppConfig.TrialProperty;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.account.AdminResponse;
import com.wmsi.sgx.model.account.TrialResponse;
import com.wmsi.sgx.security.UserDetailsWrapper;
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
	
	@RequestMapping(value="getTrial", method = RequestMethod.POST)
	public AdminResponse getTrial(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody TrialResponse response){
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.getTrialDays();
	}
	
	@RequestMapping(value= "setTrial", method = RequestMethod.POST)
	public AdminResponse setTrial(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody TrialResponse response){		
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.trialDay(response);
	}
	
	@RequestMapping(value = "findUser", method = RequestMethod.POST)
	public AdminResponse findUser(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody AdminResponse response) throws UserExistsException{
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		
		return adminService.findByUser(response.getId());
	}
	
	@RequestMapping(value = "searchDate", method = RequestMethod.POST)
	public AdminResponse searchByDate(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody AdminResponse response) throws UserExistsException{
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.searchByDate(response.getDateParam());
		
	}
	
	@RequestMapping(value = "deactivate", method = RequestMethod.POST)
	public AdminResponse deactivate(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody AdminResponse response) throws UserExistsException{
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.deactivate(response.getId());
		
	}
	
	@RequestMapping(value = "extension", method = RequestMethod.POST)
	public AdminResponse extension(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody AdminResponse response) throws UserExistsException{
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.extension(response.getId(), response.getDateParam());
		
	}
	
	@RequestMapping(value = "setAdmin", method = RequestMethod.POST)
	public AdminResponse setAdmin(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody AdminResponse response) throws UserExistsException{
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER)
			return isAdmin();
		if(acct.getType() == AccountType.MASTER){
			return adminService.setAdmin(response.getId());
		}
		
		return response;
		
	}
	
	@RequestMapping(value = "removeAdmin", method = RequestMethod.POST)
	public AdminResponse removeAdmin(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody AdminResponse response) throws UserExistsException{
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER)
			return isAdmin();
		if(acct.getType() == AccountType.MASTER){
			return adminService.removeAdmin(response.getId());
		}
		
		return response;
		
	}	
	
	public AdminResponse isAdmin(){
		AdminResponse ret = new AdminResponse();
		ret.setResponseCode(22);
		ret.setData("Account is not authorized.");
		return ret;
	}
	
	@RequestMapping(value = "excel", produces = "text/csv;charset=utf-8")
	public void excel(HttpServletResponse response) throws IOException{
		
		adminService.writeCsv(response, new String[] { "Username", "Created", "Status", "Expiration" }, "admin-excel-");
	}
	
	
	
}
