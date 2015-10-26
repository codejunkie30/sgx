 package com.wmsi.sgx.service.account.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.service.account.AcccountExiprationService;

@Service
public class AccountExpirationServiceImpl implements AcccountExiprationService{

	
	public AccountExpirationServiceImpl() {
		super();
	}

	@Autowired
	private AccountRepository accountRepository;
	

	//need to switch it to pick value from the placeholder
	//there is some issue its not picking up value form there 
	//@Scheduled(cron="0/25 * * * * ?")
	public void checkAccountExpiration() {
		List<Account> accounts = accountRepository.findAll();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
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
