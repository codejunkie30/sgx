 package com.wmsi.sgx.service.account.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.config.AppConfig.TrialProperty;
import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.service.EmailService;
import com.wmsi.sgx.service.account.AcccountExiprationService;
import com.wmsi.sgx.util.DateUtil;

@Service
public class AccountExpirationServiceImpl implements AcccountExiprationService{
	
	private static final Logger log = LoggerFactory.getLogger(AccountExpirationServiceImpl.class);

	@Autowired
	private TrialProperty getTrial;
	
	private static final int PREMIUM_EXPIRATION_DAYS = 365;
	
	public AccountExpirationServiceImpl() {
		super();
	}

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private EmailService emailService;
	
	
	@Value ("${email.halfway.expiration.notice}")
	private String halfWayTrialEmailBody;
	
	@Value ("${email.trialExpired.notice}")
	private String trialExpiredEmailBody;

	@Value ("${email.halfway.expiration.notice.subject}")
	private String halfWaySubject;
	
	@Value ("${email.trialExpired.notice.subject}")
	private String trialExpiredSubject;
	
	
	//need to switch it to pick value from the placeholder
	//there is some issue its not picking up value form there 
	//@Scheduled(cron="0 20 14 ? * *")
	
	public void checkAccountExpiration() throws MessagingException{
		List<Account> accounts = accountRepository.findAll();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		//Username Password does not exist its only for faking user to system default
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("example@gmail.com",
				"Test1234@");
		SecurityContextHolder.getContext().setAuthentication(authRequest);
		
		for( Account acc: accounts)	{
			if(acc.getAlwaysActive() == false && acc.getActive() == true){
				Date expiration = acc.getExpirationDate() != null ? acc.getExpirationDate() :
					DateUtil.toDate(DateUtil.adjustDate(DateUtil.fromDate(acc.getStartDate()), Calendar.DAY_OF_MONTH, acc.getType() == AccountType.TRIAL ? getTrial.getTrialDays() : PREMIUM_EXPIRATION_DAYS));
				
				
				if(sdf.format(expiration).compareTo(sdf.format(new Date()))<0){
					acc.setActive(false);
					acc.setExpirationDate(new Date());
					acc.setCurrency("SGD");
					accountRepository.save(acc);
					try{
					sendTrialExpiredEmail(acc.getUser().getUsername());
					
					}
					catch(MessagingException e){
						log.error("exception while sending email to "+acc.getUser().getUsername());
					}
				}
			}
		}
		
	}
	
	//@Scheduled(cron="0 20 15 ? * *")
	public void sendAccountExpirationHalfWayEmail() throws MessagingException{
		List<Account> accounts = accountRepository.findAll();
		for( Account acc: accounts)	{
			if(acc.getAlwaysActive() == false && acc.getActive()==true && acc.getType()==AccountType.TRIAL && acc.getExpirationDate() == null){
				int comparatorVal = DateTimeComparator.getDateOnlyInstance()
						.compare((new DateTime(acc.getStartDate()).plusDays(getTrial.getHalfwayDays())), new DateTime());
				if(comparatorVal==0){
				try{
					sendHalfWayTrialEmail(acc.getUser().getUsername());
					
					}
					catch(MessagingException e){
						log.error("exception while sending email to "+acc.getUser().getUsername());
					}
				}
			}
		}
	}
	
	private void sendHalfWayTrialEmail(String email) throws MessagingException{
		emailService.send(email, halfWaySubject, null, halfWayTrialEmailBody);
	}
	
	private void sendTrialExpiredEmail(String email) throws MessagingException{
		emailService.send(email, trialExpiredSubject, null, trialExpiredEmailBody);
	}
	
	
}
