package com.wmsi.sgx.service.account.impl;

import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang3.tuple.MutablePair;
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

/**
 * This class executes WatchlistEmailService and sends the notification emails.
 * The user email details save into database.
 *
 */

@Service
@DisallowConcurrentExecution
public class WatchlistEmailServiceHelper implements Job {

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

	private static final Logger log = LoggerFactory.getLogger(WatchlistEmailServiceHelper.class);

	/**
	 * Executes WatchlistEmailService and send the notification emails.
	 * 
	 * @param context
	 *            JobExecutionContext
	 * 
	 * @throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Executing WatchlistEmailService");
		String content = null;
		List<Account> accounts = accountRepository.findAll();
		for (Account acc : accounts) {
			if (acc.getActive() == true) {
				List<WatchlistModel> list = watchlistService.getWatchlist(acc.getUser());
				if (list.size() > 0)
					for (WatchlistModel watchlist : list) {
							content = null;
							MutablePair<List<AlertOption>, List<String>> options = new MutablePair<List<AlertOption>, List<String>>();
							try {
								options = watchlistEmailService.parseWatchlist(watchlist, acc);

							} catch (QuanthouseServiceException | CompanyServiceException | SearchServiceException e) {
								log.error("exception while parsing watchlist");
								insertEmailTransaction(acc.getUser(), watchlist, IEmailAuditMessages.NO_BODY,
										IEmailAuditMessages.EMAIL_SUBJECT, IEmailAuditMessages.EMAIL_FAILED,
										e.getMessage());
							}
							if (watchlist.getCompanies().size() > 0 && options.getLeft() != null
									&& options.getLeft().size() > 0) {
								try {
									List<AlertOption> options2 = options.getLeft();
									log.info(" Watch list info  \n:" + acc.getUser().getUsername() + " \t"
											+ options2.size() + "\t " + watchlist.getCompanies().size());
									content = senderService.send(acc.getUser().getUsername(),
											IEmailAuditMessages.EMAIL_SUBJECT, options2, watchlist, quanthouseService
													.getPriceChangeForWatchlistCompanies(watchlist.getCompanies()));
									insertEmailTransaction(acc.getUser(), watchlist, content,
											IEmailAuditMessages.EMAIL_SUBJECT, IEmailAuditMessages.EMAIL_SUCCESS,
											IEmailAuditMessages.EMAIL_SUCCESS);
								} catch (MessagingException | QuanthouseServiceException | CompanyServiceException
										| SearchServiceException e) {
									log.info("exception while sending watchList email to "
											+ acc.getUser().getUsername());
									log.info("Exception in email notification : ",
											e.getMessage() + "\n Details of Root cause " + e);
									insertEmailTransaction(acc.getUser(), watchlist, IEmailAuditMessages.NO_BODY,
											IEmailAuditMessages.EMAIL_SUBJECT, IEmailAuditMessages.EMAIL_FAILED,
											e.getMessage());
								}
							} else {
								if (options.getRight() != null) {
									for (Object o : options.getRight()) {
										insertEmailTransaction(acc.getUser(), watchlist, IEmailAuditMessages.NO_BODY,
												IEmailAuditMessages.EMAIL_SUBJECT, IEmailAuditMessages.EMAIL_FAILED,
												o.toString());
									}
								} else {
									insertEmailTransaction(acc.getUser(), watchlist, IEmailAuditMessages.NO_BODY,
											IEmailAuditMessages.EMAIL_SUBJECT, IEmailAuditMessages.EMAIL_FAILED,
											IEmailAuditMessages.WATCHLIST_UNAVAILABLE);
							}
						}
					}
			}
		}
	}

	/**
	 * Saves the email details into database.
	 * 
	 * @param user
	 *            User, watchlist WatchlistModel, body String, subject String,
	 *            status String, reason String
	 * 
	 */
	
	private void insertEmailTransaction(User user, WatchlistModel watchlist, String body, String subject, String status,
			String reason) {
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
