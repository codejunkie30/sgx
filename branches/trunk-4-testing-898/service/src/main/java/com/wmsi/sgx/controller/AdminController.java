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
import com.wmsi.sgx.domain.CustomAuditorAware;
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

/**
 * The AdminController class is used for performing Admin operations
 *
 */
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
	
	@Autowired
	private CustomAuditorAware<User> auditorProvider;
	
	/**
	 * Retrieves the Account information based on the X-AUTH-TOKEN provided in
	 * the HttpServletRequest.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return AccountModel
	 * @throws UserExistsException
	 *             If user doesn't exists
	 */
	@RequestMapping(value = "info", method = RequestMethod.POST)
	public @ResponseBody AccountModel account(HttpServletRequest request) throws UserExistsException{		
		return accountService.getAccountForUsername(findUserFromToken(request).getUsername());
	}
	
	/**
	 * Provides the AdminAccountModel for the Account information provided. This
	 * operation can only be performed by accounts type MASTER or ADMIN
	 * 
	 * @param HttpRequest
	 *            HttpServletRequest
	 * @param request
	 *            AdminResponse
	 * @return AdminResponse
	 */
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
	
	/**
	 * Retrieves the trial days for the user which is retrieved from
	 * X-AUTH-TOKEN. This operation can only be performed by accounts type
	 * MASTER or ADMIN
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return AdminResponse
	 */
	@RequestMapping(value="getTrial", method = RequestMethod.POST)
	public AdminResponse getTrial(HttpServletRequest request, @RequestBody TrialResponse response){
		AccountModel acct = accountService.getAccountForUsername(findUserFromToken(request).getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.getTrialDays();
	}

	/**
	 * Sets the trail days for the user which is retrieved from X-AUTH-TOKEN.
	 * This operation can only be performed by account whose type is MASTER or
	 * ADMIN
	 *
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            TrialResponse
	 * @return AdminResponse
	 */
	@RequestMapping(value= "setTrial", method = RequestMethod.POST)
	public AdminResponse setTrial(HttpServletRequest request, @RequestBody TrialResponse response){
		User user = findUserFromToken(request);
		//For Auditing
		auditorProvider.setUser(user);
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN )
			return isAdmin();
		return adminService.trialDay(response,acct.getEmail());
	}
	
	/**
	 * Retrieves the user based on X-AUTH-TOKEN provided. This operation can
	 * only be performed by accounts type MASTER or ADMIN
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            AdminResponse
	 * @return AdminResponse
	 * @throws UserExistsException
	 */
	@RequestMapping(value = "findUser", method = RequestMethod.POST)
	public AdminResponse findUser(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		AccountModel acct = accountService.getAccountForUsername(findUserFromToken(request).getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		
		return adminService.findByUser(response.getId());
	}
	
	/**
	 * Retrieves users by given date which is the creation date. This operation
	 * can only be performed by accounts type MASTER or ADMIN
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            AdminResponse
	 * @return AdminResponse
	 * @throws UserExistsException
	 */
	@RequestMapping(value = "searchDate", method = RequestMethod.POST)
	public AdminResponse searchByDate(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		AccountModel acct = accountService.getAccountForUsername(findUserFromToken(request).getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.searchByDate(response.getDateParam());
		
	}
	
	/**
	 * Deactivates the user. This operation can only be performed by accounts
	 * type MASTER or ADMIN
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            AdminResponse
	 * @return AdminResponse
	 * @throws UserExistsException
	 */
	@RequestMapping(value = "deactivate", method = RequestMethod.POST)
	public AdminResponse deactivate(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		User user = findUserFromToken(request);
		//For Auditing
		auditorProvider.setUser(user);
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.deactivate(response.getId(), user.getId());
		
	}
	
	/**
	 * Extends the expire date for the user provided. This operation can
	 * only be performed by accounts type MASTER or ADMIN
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            AdminResponse
	 * @return AdminResponse
	 * @throws UserExistsException
	 */
	@RequestMapping(value = "extension", method = RequestMethod.POST)
	public AdminResponse extension(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		User user = findUserFromToken(request);
		//For Auditing
		auditorProvider.setUser(user);
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER && acct.getType() != AccountType.ADMIN)
			return isAdmin();
		return adminService.extension(response.getId(), response.getDateParam(), user.getId());
		
	}
	
	/**
	 * Sets the user as Admin. This operation can only be performed by
	 * accounts type MASTER
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            AdminResponse
	 * @return AdminResponse
	 * @throws UserExistsException
	 */
	@RequestMapping(value = "setAdmin", method = RequestMethod.POST)
	public AdminResponse setAdmin(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		User user = findUserFromToken(request);
		//For Auditing
		auditorProvider.setUser(user);
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER)
			return isAdmin();
		if(acct.getType() == AccountType.MASTER){
			return adminService.setAdmin(response.getId(), user.getId());
		}
		
		return response;
		
	}
	
	/**
	 * Removes the admin access for the user provided. This operation can only
	 * be performed by accounts type MASTER
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            AdminResponse
	 * @return AdminResponse
	 * @throws UserExistsException
	 */
	@RequestMapping(value = "removeAdmin", method = RequestMethod.POST)
	public AdminResponse removeAdmin(HttpServletRequest request, @RequestBody AdminResponse response) throws UserExistsException{
		User user = findUserFromToken(request);
		//For Auditing
		auditorProvider.setUser(user);
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		if(acct.getType() != AccountType.MASTER)
			return isAdmin();
		if(acct.getType() == AccountType.MASTER){
			return adminService.removeAdmin(response.getId(), user.getId());
		}
		
		return response;
		
	}	
	
	/**
	 * Set the AdminResponse with response code 22 and the data as Account is
	 * not authorized.
	 * 
	 * @return AdminResponse
	 */
	public AdminResponse isAdmin(){
		AdminResponse ret = new AdminResponse();
		ret.setResponseCode(22);
		ret.setData("Account is not authorized.");
		return ret;
	}
	/**
	 * Exports the list of users into a csv file. This operation can only
	 * be performed by accounts type MASTER or ADMIN
	 * 
	 * @param token X-AUTH-TOKEN
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
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
	
	/**
	 * Retrieves the User from the X-AUTH-TOKEN provided in the
	 * HttpServletRequest header
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return User
	 */
	public User findUserFromToken(HttpServletRequest request){
		String token = request.getHeader("X-AUTH-TOKEN");
		
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		User user = null;
		if(token != null)
		 return user = tokenHandler.parseUserFromToken(token);
		return null;
	}
	
	
	
}
