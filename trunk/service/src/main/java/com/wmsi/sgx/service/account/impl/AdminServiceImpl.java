package com.wmsi.sgx.service.account.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.SortAccountByExpirationDateComparator;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.account.AdminAccountModel;
import com.wmsi.sgx.model.account.AdminResponse;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.repository.UserRepository;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.AdminService;
import com.wmsi.sgx.util.DateUtil;

@Service
public class AdminServiceImpl implements AdminService{
	
	@Value ("${full.trial.duration}")
	private int TRIAL_EXPIRATION_DAYS;
	
	private static final int PREMIUM_EXPIRATION_DAYS = 365;

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private UserRepository userReposistory;
	
	@Autowired
	private AccountService accountService;
	
	@Override
	public AdminResponse trialDay() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public AdminResponse findByUser(String user){
		AdminResponse ret = new AdminResponse();		
		User u = userReposistory.findByUsername(user);
		List<Account> accounts = accountRepository.findByUsername(user);
		Collections.sort(accounts, new SortAccountByExpirationDateComparator());
		AdminAccountModel model = new AdminAccountModel();
		if(accounts.size() != 0){
			Account curr = accounts.get(0);
			model.setUsername(u.getUsername());
			model.setCreated_date(u.getCreatedDate());
			model.setStatus(curr.getActive() ? curr.getType().toString() : "expired");
			if(curr.getActive()){
				Date exp = DateUtil.toDate(DateUtil.adjustDate(DateUtil
						.fromDate(curr.getStartDate()), Calendar.DAY_OF_MONTH, curr.getType() == AccountType.TRIAL ? TRIAL_EXPIRATION_DAYS : PREMIUM_EXPIRATION_DAYS));
				model.setExpiration_date(curr.getExpirationDate() != null ? curr.getExpirationDate() : exp);
			}
			ret.setData(model);
			ret.setResponseCode(0);
			return ret;
		}else{
			ret.setData("Account does not exist.");
			ret.setResponseCode(19);
			return ret;
		}
	}

	@Override
	public AdminResponse searchByDate(Date period) {
		AdminResponse ret = new AdminResponse();
		User[] users = userReposistory.findByDate(period);
		List<AdminAccountModel> retList = new ArrayList<AdminAccountModel>();
		for(User u : users){
			AdminAccountModel model = new AdminAccountModel();
			List<Account> accounts = accountRepository.findByUsername(u.getUsername());
			Collections.sort(accounts, new SortAccountByExpirationDateComparator());
			if(accounts.size() != 0){
				Account curr = accounts.get(0);
				model.setUsername(u.getUsername());
				model.setCreated_date(u.getCreatedDate());
				model.setStatus(curr.getActive() ? curr.getType().toString() : "expired");
				if(curr.getActive()){
					Date exp = DateUtil.toDate(DateUtil.adjustDate(DateUtil
							.fromDate(curr.getStartDate()), Calendar.DAY_OF_MONTH, curr.getType() == AccountType.TRIAL ? TRIAL_EXPIRATION_DAYS : PREMIUM_EXPIRATION_DAYS));
					model.setExpiration_date(curr.getExpirationDate() != null ? curr.getExpirationDate() : exp);
				}
				retList.add(model);
			}
		}
		ret.setData(retList);
		ret.setResponseCode(0);
		return ret;
	}

	@Override
	public AdminResponse deactivate(String username) {
		List<Account> accounts = accountRepository.findByUsername(username);
		AdminResponse ret = new AdminResponse();
		Boolean deactivated = false;
		if(accounts.size() == 0){
			ret.setResponseCode(19);
			ret.setData("Account does not exist.");
			return ret;
		}
		
		for(Account acc : accounts){
			if(acc.getActive() == true){
				acc.setActive(false);
				accountRepository.save(acc);
				deactivated = true;
			}
		}
		ret.setResponseCode(deactivated ? 0 : 20);
		ret.setData(deactivated ? "Success." : "Account already deactivated.");
		return ret;
	}

	@Override
	public AdminResponse extension(String username, Date period) {
		AdminResponse ret = new AdminResponse();
		List<Account> accounts = accountRepository.findByUsername(username);
		
		if(accounts.size() == 0){
			ret.setResponseCode(19);
			ret.setData("Account does not exist.");
			return ret;
		}
		
		Collections.sort(accounts, new SortAccountByExpirationDateComparator());
		Account edit = accounts.get(0);
		edit.setExpirationDate(period);
		System.out.println(period);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		if(sdf.format(period).compareTo(sdf.format(new Date()))>0)
			edit.setActive(true);
		
		AdminAccountModel model = new AdminAccountModel();
		model.setExpiration_date(period);
		model.setCreated_date(edit.getCreatedDate());
		model.setStatus(edit.getActive().toString());
		model.setUsername(username);
		
		accountRepository.save(edit);
		
		ret.setResponseCode(0);
		ret.setData("Success.");
		return ret;
	}
	
	@Override
	public AdminResponse setAdmin(String username){
		AdminResponse ret = new AdminResponse();
		List<Account> accounts = accountRepository.findByUsername(username);
		
		if(accounts.size() == 0){
			ret.setResponseCode(19);
			ret.setData("Account does not exist.");
			return ret;
		}
		
		Collections.sort(accounts, new SortAccountByExpirationDateComparator());
		Account edit = accounts.get(0);
		Date expiration = DateUtil.toDate(DateUtil.adjustDate(DateUtil.fromDate(edit.getStartDate()), Calendar.DAY_OF_MONTH, edit.getType() == AccountType.TRIAL ? 
				TRIAL_EXPIRATION_DAYS : PREMIUM_EXPIRATION_DAYS));
		edit.setType(AccountType.ADMIN);
		edit.setActive(true);
		edit.setAlwaysActive(true);
		accountRepository.save(edit);
		
		AdminAccountModel model = new AdminAccountModel();
		model.setExpiration_date(expiration);
		model.setCreated_date(edit.getCreatedDate());
		model.setStatus(edit.getType().toString());
		model.setUsername(username);
		
		ret.setResponseCode(0);
		ret.setData(model);
		return ret;		
	}
	
	@Override
	public AdminResponse removeAdmin(String username){
		AdminResponse ret = new AdminResponse();
		List<Account> accounts = accountRepository.findByUsername(username);
		
		if(accounts.size() == 0){
			ret.setResponseCode(19);
			ret.setData("Account does not exist.");
			return ret;
		}
		
		Collections.sort(accounts, new SortAccountByExpirationDateComparator());
		Account edit = accounts.get(0);
		Date expiration = DateUtil.toDate(DateUtil.adjustDate(DateUtil.fromDate(edit.getStartDate()), Calendar.DAY_OF_MONTH, edit.getType() == AccountType.TRIAL ? 
				TRIAL_EXPIRATION_DAYS : PREMIUM_EXPIRATION_DAYS));
		edit.setType(AccountType.PREMIUM);
		edit.setAlwaysActive(false);
		edit.setExpirationDate(null);
		accountRepository.save(edit);
		
		AdminAccountModel model = new AdminAccountModel();
		model.setExpiration_date(expiration);
		model.setCreated_date(edit.getCreatedDate());
		model.setStatus(edit.getType().toString());
		model.setUsername(username);
		
		ret.setResponseCode(0);
		ret.setData(model);
		return ret;		
	}

}
