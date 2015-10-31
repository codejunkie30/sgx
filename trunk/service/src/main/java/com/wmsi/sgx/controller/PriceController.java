package com.wmsi.sgx.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.search.IdSearch;
import com.wmsi.sgx.repository.UserRepository;
import com.wmsi.sgx.security.UserDetailsWrapper;
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
	
	private String market = "XSES";
	
	@RequestMapping(value="/price")
	public Map<String, Price> getPrice(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody IdSearch query) throws CompanyServiceException {
		Price p = new Price();
		
		AccountModel acct = accountService.getAccountForUsername(user.getUsername());
		Map<String, Price> ret = new HashMap<String, Price>();
					
		try{			
			if(acct.getType().equals(AccountType.PREMIUM))
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


}

