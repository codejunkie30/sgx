package com.wmsi.sgx.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.security.UserDetailsWrapper;
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
	@RequestMapping("search")
	public SearchResults search(@Valid @RequestBody SearchRequest req) throws ServiceException{

		User u = null;
		AccountType accountType=null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null|| !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)){
			
			accountType = AccountType.NOT_LOGGED_IN;	
		}else{
			u = ((UserDetailsWrapper) authentication.getPrincipal()).getUser();
			AccountModel accountModel =  accountService.getAccountForUsername(u.getUsername());
			accountType = accountModel.getType();
		}
			
		
		return companySearchService.search(req,accountType);
	}
}
