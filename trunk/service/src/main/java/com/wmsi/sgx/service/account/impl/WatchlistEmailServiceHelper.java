package com.wmsi.sgx.service.account.impl;

import java.util.List;

import javax.mail.MessagingException;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.EmailAudit;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.AlertOption;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.repository.EmailAuditRepository;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.WatchlistSenderService;
import com.wmsi.sgx.service.account.QuanthouseService;
import com.wmsi.sgx.service.account.QuanthouseServiceException;
import com.wmsi.sgx.service.account.WatchlistEmailService;
import com.wmsi.sgx.service.account.WatchlistService;
import com.wmsi.sgx.service.search.SearchServiceException;

@Service
@DisallowConcurrentExecution
public class WatchlistEmailServiceHelper implements Job{
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private WatchlistService watchlistService;
	
	@Autowired
	private WatchlistSenderService senderService;
	
	@Autowired
	private WatchlistEmailService watchlistEmailService;
	
	@Autowired
	private QuanthouseService quanthouseService;
	
	@Autowired
	private EmailAuditRepository emailAuditRepository;
	
	private static final String EMAIL_SUBJECT = "SGX StockFacts Premium Alert"; 
	
	private static final String EMAIL_SUCCESS = "Success";
	
	private static final String EMAIL_FAILED = "Failed";
	
	private static final String WATCHLIST_UNAVAILABLE = "Watchlist Unavailable";
	
	private static final Logger log = LoggerFactory.getLogger(WatchlistEmailServiceHelper.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Executing WatchlistEmailService");
		String content = null;
		List<Account> accounts = accountRepository.findAll();
		for( Account acc: accounts)	{
			if(acc.getActive() == true){
				List<WatchlistModel> list =	watchlistService.getWatchlist(acc.getUser());
				if(list.size() > 0)
					for(WatchlistModel watchlist : list){
						List<AlertOption> options=null;
						try {
							options = watchlistEmailService.parseWatchlist(watchlist, acc);
						} catch (QuanthouseServiceException | CompanyServiceException | SearchServiceException e) {
							log.error("exception while parsing watchlist");
						}
						if(watchlist.getCompanies().size() > 0 && options.size() > 0){
							try {
								log.info(" Watch list info  \n:" + acc.getUser().getUsername() +  " \t" +
										options.size() + "\t "+ watchlist.getCompanies().size() );
								
								content = senderService.send(acc.getUser().getUsername(), EMAIL_SUBJECT, options, watchlist, quanthouseService.getCompanyPrice(watchlist.getCompanies()));
								
								insertEmailTransaction(acc.getUser(), watchlist, content, EMAIL_SUBJECT, EMAIL_SUCCESS, EMAIL_SUCCESS);
								
							} catch (MessagingException | QuanthouseServiceException | CompanyServiceException | SearchServiceException e) {
								log.info("exception while sending watchList email to "+acc.getUser().getUsername());
								log.info("Exception in email notification : ",e.getMessage() + "\n Details of Root cause " + e);
								
								insertEmailTransaction(acc.getUser(), watchlist, content, EMAIL_SUBJECT, EMAIL_FAILED, e.getMessage());
								
							}
						}else{
							insertEmailTransaction(acc.getUser(), watchlist, content, EMAIL_SUBJECT, EMAIL_FAILED, WATCHLIST_UNAVAILABLE);
						}
					}
			}
		}
	}

	private void insertEmailTransaction(User user, WatchlistModel watchlist, String body, String subject, String status, String reason) {
		log.info("Inside the insertEmailTransaction method");
		EmailAudit emailAudit = new EmailAudit();
		emailAudit.setUserId(user.getId());
		emailAudit.setSubject(subject);
		emailAudit.setBody(body);
		emailAudit.setEmail(user.getUsername());
		emailAudit.setReason(reason);
		emailAudit.setStatus(status);
		emailAudit.setWatchlistName(watchlist.getName());
		emailAuditRepository.save(emailAudit); 
	}
}

