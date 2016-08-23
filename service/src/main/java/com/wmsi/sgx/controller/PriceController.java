package com.wmsi.sgx.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TokenHandler;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.QuanthouseService;
import com.wmsi.sgx.service.account.QuanthouseServiceException;
import com.wmsi.sgx.service.search.SearchServiceException;


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
	public Map<String, Price> getPrice(HttpServletRequest request, @RequestBody IdSearch query) throws CompanyServiceException, SearchServiceException {
		log.info("price requested for id: --{}--",query.getId() );
		if(query.getId()== null || query.getId().isEmpty()){
			throw new CompanyServiceException("Request company ticker is null");
		}
		Price p = new Price();
		User user = findUserFromToken(request);
		AccountModel acct = new AccountModel();
		if(user != null)
			acct = accountService.getAccountForUsername(user.getUsername());
		else
			acct.setType(AccountType.TRIAL);
		Map<String, Price> ret = new HashMap<String, Price>();
					
		try{			
			if(acct.getType().equals(AccountType.PREMIUM) || acct.getType().equals(AccountType.ADMIN)
					|| acct.getType().equals(AccountType.MASTER))
				p = service.getPrice(market, query.getId());
			else{
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, -15);
				Date delayedTime = cal.getTime();
				p = service.getPriceAt(market, query.getId(), delayedTime);
				
			}
		}
		catch(QuanthouseServiceException e){
			log.error("Failed to get intra day price from QuanthouseService");
		}
		ret.put("price", p);		
		
		return ret;
	}
	
	@RequestMapping(value="/price/companyPrices")
	public Map<String, List<CompanyPrice>> getCompanyPrices(@RequestBody WatchlistAddCompany companies) throws QuanthouseServiceException, CompanyServiceException, SearchServiceException{
		Map<String, List<CompanyPrice>> ret = new HashMap<String, List<CompanyPrice>>();		
		
		ret.put("companyPrice", service.getCompanyPrice(companies.getCompanies()));
		
		
		return ret;
		
	}

	@RequestMapping(value="/price/intraday")
	public List<Price> getIntradayPrices(@RequestBody IdSearch query) {
		List<Price> prices = null;
		
		try{
			prices = service.getIntradayPrices(market, query.getId());
			
		}
		catch(QuanthouseServiceException e){
			log.error("Failed to get intra day price from QuanthouseService", e);
		}
		
		return prices;
	}
	
	@RequestMapping(value="/price/pricingHistory")
	public Map<String, List<Price>> getPricingHistory(HttpServletRequest request, @RequestBody PriceCall priceCall) throws QuanthouseServiceException, CompanyServiceException{
		log.info("Real time price requested for id: --{}--",priceCall.getId() );
		if(priceCall.getId()== null || priceCall.getId().isEmpty()){
			throw new CompanyServiceException("Request company ticker is null");
		}
		Price p;
		User user = findUserFromToken(request);
		AccountModel acct = new AccountModel();
		if(user != null)
			acct = accountService.getAccountForUsername(user.getUsername());
		Map<String, List<Price>> ret = new HashMap<String, List<Price>>();
		List<Price> price = new ArrayList<>();
					
		try {
			if(user == null || AccountType.EXPIRED.equals(acct.getType())){
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, -15);
				Date delayedTime = cal.getTime();
				p = service.getPriceAt(market, priceCall.getId(), delayedTime);
				price.add(p);
			}else{
				if (acct.getType().equals(AccountType.PREMIUM) || acct.getType().equals(AccountType.ADMIN)
						|| acct.getType().equals(AccountType.MASTER)) {
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.MINUTE, -1);
					Date date = new DateTime(cal.getTime()).toDate();
					price = service.getPricingHistory(market, priceCall.getId(), date);
				} else if(acct.getType().equals(AccountType.TRIAL)){
					Calendar fromCal = Calendar.getInstance();
					fromCal.add(Calendar.MINUTE, -16);
					Date fromDate = new DateTime(fromCal.getTime()).toDate();
					
					Calendar toCal = Calendar.getInstance();
					toCal.add(Calendar.MINUTE, -15);
					Date toDate = new DateTime(toCal.getTime()).toDate();
					price = service.getPricingHistoryBetweenDates(market, priceCall.getId(), fromDate, toDate);

				}
			}
		}
		catch(QuanthouseServiceException | SearchServiceException e){
			log.error("Failed to get intra day price from QuanthouseService");
		}
		ret.put("pricingHistory", price);		
		
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

}

