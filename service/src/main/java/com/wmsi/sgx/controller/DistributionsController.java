package com.wmsi.sgx.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wiz.enets2.transaction.umapi.logging.logger;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.distribution.Distributions;
import com.wmsi.sgx.model.distribution.DistributionsRequest;
import com.wmsi.sgx.security.UserDetailsWrapper;
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TokenHandler;
import com.wmsi.sgx.service.DistributionService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.account.AccountService;

/**
 * This controller is used to fetch the distributions information of various companies.
 *
 */
@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class DistributionsController{
	private static final Logger log = LoggerFactory.getLogger(DistributionsController.class);
	
	@Autowired 
	private DistributionService distributionService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;	
  
	/**
	 * Retrieves the distribution information of all the companies.
	 * 
	 * @param req DistributionsRequest
	 * @param request HttpServletRequest
	 * @return Distributions
	 * @throws ServiceException
	 */
	@RequestMapping(value="search/distributions", method = RequestMethod.POST)
	public Distributions postChartHistograms(@Valid @RequestBody DistributionsRequest req, HttpServletRequest request) throws ServiceException{		
		User u = null;
		AccountType accountType=null;
		
		String token = request.getHeader("X-AUTH-TOKEN");
		
		String currency ;
		if(request.getHeader("currency") != null)
			currency= request.getHeader("currency");
		else
			currency="sgd";
		log.info("Initial currency ", currency);
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		
		if(token != null){
		u = tokenHandler.parseUserFromToken(token);
		AccountModel accountModel =  accountService.getAccountForUsername(u.getUsername());
		accountType = accountModel.getType();
				
		}else{
			
			accountType = AccountType.NOT_LOGGED_IN;
		}
			
		return distributionService.getAggregations(req, currency, accountType);
	}	
}
