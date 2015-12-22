package com.wmsi.sgx.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TokenHandler;
import com.wmsi.sgx.service.CompanySearchService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.account.AccountService;

@RestController()
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class SearchController{

	@Autowired
	private CompanySearchService companySearchService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@RequestMapping("search")
	public SearchResults search(@Valid @RequestBody SearchRequest req, HttpServletRequest request) throws ServiceException{

		User u = null;
		AccountType accountType=null;
		String token = request.getHeader("X-AUTH-TOKEN");
		
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		
		if(token != null){
		u = tokenHandler.parseUserFromToken(token);
		AccountModel accountModel =  accountService.getAccountForUsername(u.getUsername());
		accountType = accountModel.getType();
		}else{
			
			accountType = AccountType.NOT_LOGGED_IN;
		}
			
		
		return companySearchService.search(req,accountType);
	}
}
