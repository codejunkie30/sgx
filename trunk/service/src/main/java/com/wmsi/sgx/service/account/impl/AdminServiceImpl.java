package com.wmsi.sgx.service.account.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVWriter;

import com.wmsi.sgx.config.AppConfig.TrialProperty;
import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.SortAccountByExpirationDateComparator;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.domain.UserLogin;
import com.wmsi.sgx.model.account.AdminAccountModel;
import com.wmsi.sgx.model.account.AdminResponse;
import com.wmsi.sgx.model.account.TrialResponse;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.repository.UserLoginRepository;
import com.wmsi.sgx.repository.UserRepository;
import com.wmsi.sgx.service.PropertiesService;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.AdminService;
import com.wmsi.sgx.util.DateUtil;

@Service
public class AdminServiceImpl implements AdminService{
	
	@Autowired
	private TrialProperty getTrial;
	
	private static final int PREMIUM_EXPIRATION_DAYS = 365;

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private UserRepository userReposistory;
	
	@Autowired
	private UserLoginRepository userLoginReposistory;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired 
	private PropertiesService propertiesService;
	
	@Override
	public AdminResponse getTrialDays(){
		AdminResponse ret = new AdminResponse();
		TrialResponse resp = new TrialResponse();
		resp.setHalfwayDays(getTrial.getHalfway());
		resp.setTrialDays(getTrial.getTrial());
		ret.setData(resp);
		ret.setResponseCode(0);
		
		return ret;
		
	}
	
	@Override
	public AdminResponse trialDay(TrialResponse response) {
		AdminResponse ret = new AdminResponse();
		if(response.getHalfwayDays() == null || response.getTrialDays() == null){
			ret.setData("Invalid/missing trial or halfway days.");
			ret.setResponseCode(23);
			return ret;
		}
		propertiesService.setProperty("full.trial.duration", response.getTrialDays());
		propertiesService.setProperty("halfway.trial.duration", response.getHalfwayDays());
		getTrial.destroy();
		getTrial.init();
		propertiesService.save();
		
		ret.setResponseCode(0);
		ret.setData(response);
		return ret;
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
			Date exp = DateUtil.toDate(DateUtil.adjustDate(DateUtil
					.fromDate(curr.getStartDate()), Calendar.DAY_OF_MONTH, curr.getType() == AccountType.TRIAL ? getTrial.getTrialDays() : PREMIUM_EXPIRATION_DAYS));
			model.setExpiration_date(curr.getExpirationDate() != null ? curr.getExpirationDate() : exp);		
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
				Date exp = DateUtil.toDate(DateUtil.adjustDate(DateUtil
					.fromDate(curr.getStartDate()), Calendar.DAY_OF_MONTH, curr.getType() == AccountType.TRIAL ? getTrial.getTrialDays() : PREMIUM_EXPIRATION_DAYS));
				model.setExpiration_date(curr.getExpirationDate() != null ? curr.getExpirationDate() : exp);
				retList.add(model);
			}
		}
		ret.setData(retList);
		ret.setResponseCode(0);
		return ret;
	}
	
	private List<String[]> getList(Date period){
		User[] users = userReposistory.findByDate(period);
		List<String[]> ret = new ArrayList<String[]>();
		//Email / Status / Last Login / Last Payment / Trial Start / Trial Exp / Opted / Premium Start / Premium Expiration		
		for(User u : users){
			List<UserLogin> logins = userLoginReposistory.findByUsername(u.getUsername());
			List<Account> accounts = accountRepository.findByUsername(u.getUsername());
			Collections.sort(accounts, new SortAccountByExpirationDateComparator());
			String email = "";
			String status = "";
			String lastLogin = "";
			String lastPayment = "";
			String trialStart = "";
			String trialExpiration = "";
			String emailOpted = "";
			String premiumStart = "";
			String premiumExpiration = "";
			
			if(accounts.size() != 0){
				Account lastAcct = accounts.get(accounts.size() - 1);
				Account curr = accounts.get(0);				
				Date expCheck = getExpirationDate(curr);
				
				email = u.getUsername();
				status = curr.getActive() ? curr.getType().toString() : "expired";
				if(logins.size() > 0)
					lastLogin = dateFmt(logins.get(0).getDate());
				trialStart = dateFmt(u.getCreatedDate());
				trialExpiration = lastAcct.getType() == AccountType.TRIAL ? dateFmt(getExpirationDate(lastAcct)) : "";
				emailOpted = u.getContactOptIn() ? "YES": "NO";
				if(accounts.size() > 1){
					lastPayment = dateFmt(curr.getCreatedDate());
					premiumStart = dateFmt(curr.getCreatedDate());
					premiumExpiration = dateFmt(expCheck);
				}
				
				String[] values = new String[]{email, status, lastLogin, lastPayment, trialStart, trialExpiration, emailOpted, premiumStart, premiumExpiration};
				ret.add(values);
			}
		}
		return ret;
	}
	
	private Date getExpirationDate(Account acct){
		Date exp = DateUtil.toDate(DateUtil.adjustDate(DateUtil
				.fromDate(acct.getStartDate()), Calendar.DAY_OF_MONTH, acct.getType() == AccountType.TRIAL ? getTrial.getTrialDays() : PREMIUM_EXPIRATION_DAYS));
		Date expCheck = acct.getExpirationDate() != null ? acct.getExpirationDate() : exp;
		
		return expCheck;
	}

	@Override
	public AdminResponse deactivate(String username) {
		List<Account> accounts = accountRepository.findByUsername(username);
		User u = userReposistory.findByUsername(username);
		AdminResponse ret = new AdminResponse();
		Boolean deactivated = false;
		if(accounts.size() == 0){
			ret.setResponseCode(19);
			ret.setData("Account does not exist.");
			return ret;
		}
		
		AdminAccountModel model = new AdminAccountModel();
		model.setUsername(u.getUsername());
		model.setCreated_date(u.getCreatedDate());
		
		for(Account acc : accounts){
			if(acc.getActive() == true){
				acc.setActive(false);
				acc.setExpirationDate(new Date());
				model.setStatus("EXPIRED");
				model.setExpiration_date(acc.getExpirationDate());
				accountRepository.save(acc);
				deactivated = true;
			}
		}
		ret.setResponseCode(deactivated ? 0 : 20);
		ret.setData(deactivated ? model : "Account already deactivated.");
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		if(sdf.format(period).compareTo(sdf.format(new Date()))>0)
			edit.setActive(true);
		
		AdminAccountModel model = new AdminAccountModel();
		model.setExpiration_date(period);
		model.setCreated_date(edit.getCreatedDate());
		model.setStatus(edit.getType().toString());
		model.setUsername(username);
		
		accountRepository.save(edit);
		
		ret.setResponseCode(0);
		ret.setData(model);
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
				getTrial.getTrialDays() : PREMIUM_EXPIRATION_DAYS));
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
				getTrial.getTrialDays() : PREMIUM_EXPIRATION_DAYS));
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
	
	@Override
	public void writeCsv(HttpServletResponse response, String[] header, String name)
			throws IOException {
		
		Date date = new Date();
		date.setTime(946684800000L);
		
		List<String[]> values = getList(date);
				
		response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "_" + dateFmt(new Date()) + ".csv\"");
		OutputStream resOs = response.getOutputStream();
		OutputStream buffOs = new BufferedOutputStream(resOs);
		OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);

		CSVWriter writer = new CSVWriter(outputwriter, ',', CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\r\n");
				
		try{
			writer.writeNext(header);
			writer.writeAll(values);
			outputwriter.flush();
		}

		finally{
			outputwriter.close();
			writer.close();
		}
		
		
	}

	private String dateFmt(Date d) {
		return new SimpleDateFormat("yyyy-MM-dd").format(d);
	}

}
