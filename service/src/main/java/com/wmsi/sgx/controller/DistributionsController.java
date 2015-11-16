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
import com.wmsi.sgx.model.distribution.Distributions;
import com.wmsi.sgx.model.distribution.DistributionsRequest;
import com.wmsi.sgx.security.UserDetailsWrapper;
import com.wmsi.sgx.service.DistributionService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.account.AccountService;

@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class DistributionsController{
	
	@Autowired 
	private DistributionService distributionService;
	
	@Autowired
	private AccountService accountService;

	@RequestMapping(value="search/distributions", method = RequestMethod.POST)
	public Distributions postChartHistograms(@Valid @RequestBody DistributionsRequest req) throws ServiceException{		
		User u = null;
		AccountType accountType=null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication.getPrincipal() instanceof UserDetailsWrapper){
			u = ((UserDetailsWrapper) authentication.getPrincipal()).getUser();
			AccountModel accountModel =  accountService.getAccountForUsername(u.getUsername());
			accountType = accountModel.getType();
				
		}else{
			
			accountType = AccountType.NOT_LOGGED_IN;
		}
			
		return distributionService.getAggregations(req,accountType);
	}	
}
