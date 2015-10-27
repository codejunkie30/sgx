 package com.wmsi.sgx.service.account.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.service.account.AcccountExiprationService;
import com.wmsi.sgx.service.account.UserService;

@Service
public class AccountExpirationServiceImpl implements AcccountExiprationService{

	
	public AccountExpirationServiceImpl() {
		super();
	}

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private UserService userRepository;
	
	//need to switch it to pick value from the placeholder
	//there is some issue its not picking up value form there 
	@Scheduled(cron="0 20 14 ? * *")
	public void checkAccountExpiration() {
		List<Account> accounts = accountRepository.findAll();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		//Username Password does not exist its only for faking user to system default
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("example@gmail.com",
				"Test1234@");
		
		SecurityContextHolder.getContext().setAuthentication(authRequest);
		
		for( Account acc: accounts)	{
			if(acc.getAlwaysActive() == false){
				if(sdf.format(acc.getExpirationDate()).compareTo(sdf.format(new Date()))<0){
					acc.setActive(false);
					accountRepository.save(acc);
				}
			}
		}
		
	}
}
