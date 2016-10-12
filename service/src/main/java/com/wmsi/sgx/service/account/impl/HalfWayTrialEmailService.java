package com.wmsi.sgx.service.account.impl;


import java.util.List;

import javax.mail.MessagingException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.config.AppConfig.TrialProperty;
import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.service.EmailService;

/**
 * This class executing HalfWayTrialEmailService and send email for trails
 *
 */

@Service
@DisallowConcurrentExecution
public class HalfWayTrialEmailService implements Job{
	
	private static final Logger log = LoggerFactory.getLogger(HalfWayTrialEmailService.class);

	@Autowired
	private TrialProperty getTrial;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	EmailService emailService;
	
	@Value ("${email.halfway.expiration.notice}")
	private String halfWayTrialEmailBody;
	
	@Value ("${email.halfway.expiration.notice.subject}")
	private String halfWaySubject;
	
	/**
	 * Executes HalfWayTrialEmailService.
	 * 
	 * @param context
	 *            JobExecutionContext
	 * 
	 * @throws JobExecutionException
	 */
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Executing HalfWayTrialEmailService");
		List<Account> accounts = accountRepository.findAll();
		for( Account acc: accounts)	{
			if(acc.getAlwaysActive() == false && acc.getActive()==true && acc.getType()==AccountType.TRIAL){
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
	
	/**
	 * Sends email for trails.
	 * 
	 * @param email
	 *            String
	 * 
	 * @throws MessagingException
	 */
	
	private void sendHalfWayTrialEmail(String email) throws MessagingException{
		emailService.send(email, halfWaySubject, null, halfWayTrialEmailBody);
	}

}
