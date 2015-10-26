package com.wmsi.sgx.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
	public SearchResults search(@AuthenticationPrincipal UserDetailsWrapper user,@Valid @RequestBody SearchRequest req) throws ServiceException{
		AccountModel ac =  accountService.getAccountForUsername(user.getUsername());
		return companySearchService.search(req,ac.getType());
	}
}
