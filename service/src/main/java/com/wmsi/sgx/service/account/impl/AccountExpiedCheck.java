package com.wmsi.sgx.service.account.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.service.EmailService;


@Service
@DisallowConcurrentExecution
public class AccountExpiedCheck implements Job{
	
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	EmailService emailService;
	
	@Value ("${email.trialExpired.notice.subject}")
	private String trialExpiredSubject;
	
	@Value ("${email.trialExpired.notice}")
	private String trialExpiredEmailBody;
	
	private static final Logger log = LoggerFactory.getLogger(AccountExpiedCheck.class);
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		log.info("Executing Account Expired Check");
		List<Account> accounts = accountRepository.findAll();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		//Username Password does not exist its only for faking user to system default
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("example@gmail.com",
				"Test1234@");
		SecurityContextHolder.getContext().setAuthentication(authRequest);
		
		for( Account acc: accounts)	{
			if(acc.getAlwaysActive() == false && acc.getActive() == true){
				if(sdf.format(acc.getExpirationDate()).compareTo(sdf.format(new Date()))<0){
					acc.setActive(false);
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
	
	private void sendTrialExpiredEmail(String email) throws MessagingException{
		emailService.send(email, trialExpiredSubject, null, trialExpiredEmailBody);
	}
	
}
