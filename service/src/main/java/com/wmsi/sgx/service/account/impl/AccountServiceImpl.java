package com.wmsi.sgx.service.account.impl;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.UpdateAccountModel;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.service.account.AccountCreationException;
import com.wmsi.sgx.service.account.AccountService;

@Service
public class AccountServiceImpl implements AccountService{

	@Autowired
	private AccountRepository accountRepository;

	private static final int TRIAL_EXPIRATION_DAYS = 14;
	private static final int PREMIUM_EXPIRATION_DAYS = 365;
	
	/*
	 * TODO
	 * Add constraint to account table for unique user, accountType, active?
	 * Add unique constraint user/active
	 * Add method to repo for findByUserTypeActive
	 */
	
	@Override
	@Secured("ROLE_USER")
	public AccountModel getAccountForUsername(String username){
		
		// TODO, ensure there is only ever one active account
		List<Account> accounts = accountRepository.findByUsername(username);		
		AccountModel ret = null;
		if(accounts.size() > 0){
			Account account = accounts.get(0);
			ret = new AccountModel();
			ret.setEmail(account.getUser().getUsername());
			ret.setStartDate(account.getStartDate());
			ret.setExpirationDate(account.getExpirationDate());
			ret.setType(account.getType());
			ret.setContactOptIn(account.getContactOptIn());
			ret.setCurrency(account.getCurrency());
		}
			
			return ret;
	}
	
	@Override
	@Secured("ROLE_USER")
	public AccountModel updateAccount(UpdateAccountModel dto){
		
		List<Account> accounts = accountRepository.findByUsername(dto.getEmail());		
		
		accounts.get(0).setContactOptIn(dto.getContactOptIn());
		accounts.get(0).setCurrency(dto.getCurrency());
		accountRepository.save(accounts.get(0));
		
		return getAccountForUsername(dto.getEmail());
		
	}
	
	
	@Override
	public Account createTrialAccount(User user) throws AccountCreationException{
	
		List<Account> accounts = accountRepository.findAllByUser(user);
		
		// Sanity check, there shouldn't be any other accounts yet. 
		if(accounts.size() > 0)
			throw new AccountCreationException();
			
		return createAccount(user, AccountType.TRIAL, TRIAL_EXPIRATION_DAYS);
		
	}
	
	@Override
	public Account createPremiumAccount(User user){
		
		List<Account> accounts = accountRepository.findAllByUser(user);
		
		for(Account acc : accounts){
			
			// Deactivate trial account
			if(acc.getType().equals(AccountType.TRIAL)){
				acc.setActive(false);
				accountRepository.save(acc);
			}
		}
		
		return createAccount(user, AccountType.PREMIUM, PREMIUM_EXPIRATION_DAYS);
		
	}
	
	private Account createAccount(User user, AccountType type, int expirationDays){

		Account acc = new Account();
		acc.setType(type);
		acc.setUser(user);
		acc.setStartDate(new Date());
		acc.setExpirationDate(new DateTime().plusDays(expirationDays).toDate());
		acc.setActive(true);
		acc.setAlwaysActive(false);
		acc.setContactOptIn(user.getContactOptIn());
		
		return accountRepository.save(acc);
		
	}
}
