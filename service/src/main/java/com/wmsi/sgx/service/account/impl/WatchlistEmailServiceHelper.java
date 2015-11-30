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
import com.wmsi.sgx.model.AlertOption;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.repository.AccountRepository;
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
	
	private static final Logger log = LoggerFactory.getLogger(WatchlistEmailServiceHelper.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("Entereedd cron watchlist");
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
						if(watchlist.getCompanies().size() > 0 && options.size() > 0)
							try {
								senderService.send(acc.getUser().getUsername(), "SGX StockFacts Premium Alert", options, watchlist, quanthouseService.getCompanyPrice(watchlist.getCompanies(), true));
							} catch (MessagingException | QuanthouseServiceException | CompanyServiceException e) {
								log.error("exception while sending watchList email to "+acc.getUser().getUsername());
							}
					}
			}
		}
	}
}

