package com.wmsi.sgx.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.model.PriceCall;
import com.wmsi.sgx.model.WatchlistAddCompany;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.search.CompanyPrice;
import com.wmsi.sgx.model.search.IdSearch;
import com.wmsi.sgx.security.UserDetailsWrapper;
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TokenHandler;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.QuanthouseService;
import com.wmsi.sgx.service.account.QuanthouseServiceException;


@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class PriceController {

	private Logger log = LoggerFactory.getLogger(PriceController.class);
	
	@Autowired
	private QuanthouseService service;
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	private String market = "XSES";	
	
	@RequestMapping(value="/price")
	public Map<String, Price> getPrice(HttpServletRequest request, @RequestBody IdSearch query) throws CompanyServiceException {
		Price p = new Price();
		User user = findUserFromToken(request);
		AccountModel acct = new AccountModel();
		setCurrency(request);
		if(user != null)
			acct = accountService.getAccountForUsername(user.getUsername());
		else
			acct.setType(AccountType.TRIAL);
		Map<String, Price> ret = new HashMap<String, Price>();
					
		try{			
			if(acct.getType().equals(AccountType.PREMIUM) || acct.getType().equals(AccountType.ADMIN))
				p = service.getPrice(market, query.getId());
			else{
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, -15);
				Date delayedTime = cal.getTime();
				p = service.getPriceAt(market, query.getId(), delayedTime);
			}
		}
		catch(QuanthouseServiceException e){
			log.error("Failed to get intra day price from QuanthouseService", e);
		}
		ret.put("price", p);		
		
		return ret;
	}
	
	
	
	@RequestMapping(value="/price/companyPrices")
	public Map<String, List<CompanyPrice>> getCompanyPrices(HttpServletRequest request,@RequestBody WatchlistAddCompany companies) throws QuanthouseServiceException, CompanyServiceException{
		Map<String, List<CompanyPrice>> ret = new HashMap<String, List<CompanyPrice>>();		
		setCurrency(request);
		ret.put("companyPrice", service.getCompanyPrice(companies.getCompanies()));
		
		
		return ret;
		
	}

	@RequestMapping(value="/price/intraday")
	public List<Price> getIntradayPrices(HttpServletRequest request,@RequestBody IdSearch query) {
		List<Price> prices = null;
		setCurrency(request);
		try{
			prices = service.getIntradayPrices(market, query.getId());
			
		}
		catch(QuanthouseServiceException e){
			log.error("Failed to get intra day price from QuanthouseService", e);
		}
		
		return prices;
	}
	
	@RequestMapping(value="/price/pricingHistory")
	public Map<String, List<Price>> getPricingHistory(HttpServletRequest request,@RequestBody PriceCall priceCall) throws QuanthouseServiceException{
		Map<String, List<Price>> ret = new HashMap<String, List<Price>>();
		setCurrency(request);
		ret.put("pricingHistory", service.getPricingHistory(market, priceCall.getId(), priceCall.getDate()));
		return ret;
		
	}
	
	public User findUserFromToken(HttpServletRequest request){
		String token = request.getHeader("X-AUTH-TOKEN");
		
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		User user = null;
		if(token != null)
		 return user = tokenHandler.parseUserFromToken(token);
		return null;
	}
	
	public void setCurrency(HttpServletRequest request){
		if(request.getHeader("currency") != null){
			service.setCurrency(request.getHeader("currency"));
		}else
			service.setCurrency("SGD");
	}

}

