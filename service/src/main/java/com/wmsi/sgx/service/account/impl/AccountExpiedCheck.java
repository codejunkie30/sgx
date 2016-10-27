package com.wmsi.sgx.service.account.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import com.wmsi.sgx.config.AppConfig.TrialProperty;
import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.domain.CustomAuditorAware;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.service.EmailService;
import com.wmsi.sgx.util.DateUtil;

/**
 * This class verifies the account expiration and send the trail users expired
 * email with subject and email body.
 *
 */

@Service
@DisallowConcurrentExecution
public class AccountExpiedCheck implements Job {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	EmailService emailService;

	@Value("${email.trialExpired.notice.subject}")
	private String trialExpiredSubject;

	@Value("${email.trialExpired.notice}")
	private String trialExpiredEmailBody;

	@Autowired
	private CustomAuditorAware<User> auditorProvider;

	@Autowired
	private TrialProperty getTrial;

	private static final int PREMIUM_EXPIRATION_DAYS = 365;

	private static final Logger log = LoggerFactory.getLogger(AccountExpiedCheck.class);

	/**
	 * Executes account expiration check and verify the Username Password exist
	 * in system.
	 * 
	 * @param context
	 *            JobExecutionContext
	 * @throws JobExecutionException
	 */

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		log.info("Executing Account Expired Check");
		List<Account> accounts = accountRepository.findAll();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		// Username Password does not exist its only for faking user to system
		// default
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("example@gmail.com",
				"Test1234@");
		SecurityContextHolder.getContext().setAuthentication(authRequest);

		for (Account acc : accounts) {

			if (acc.getAlwaysActive() && acc.getActive() && acc.getType() == AccountType.ADMIN
					&& getDaysRemaining(acc) <= -1) {
				acc.setAlwaysActive(false);
				acc.setType(AccountType.PREMIUM);
				User user = new User();
				user.setId(1L);
				auditorProvider.setUser(user);
				acc = accountRepository.save(acc);
			}

			if (acc.getAlwaysActive() == false && acc.getActive() == true) {

				if (getDaysRemaining(acc) <= -1) {
					acc.setActive(false);
					acc.setExpirationDate(new Date());
					accountRepository.updateAccountDeactivate(acc.getActive(), acc.getExpirationDate(),
							acc.getUser().getId(), "SGD", 1, new Date());
					try {
						sendTrialExpiredEmail(acc.getUser().getUsername());

					} catch (MessagingException e) {
						log.error("exception while sending email to " + acc.getUser().getUsername());
					}
				}
			}
		}

	}

	/**
	 * @param acc
	 * @return
	 */
	private long getDaysRemaining(Account acc) {
		Date expiration = acc.getExpirationDate() != null ? acc.getExpirationDate()
				: DateUtil.toDate(DateUtil.adjustDate(DateUtil.fromDate(acc.getStartDate()), Calendar.DAY_OF_MONTH,
						acc.getType() == AccountType.TRIAL ? getTrial.getTrialDays() : PREMIUM_EXPIRATION_DAYS));
		return DateUtil.getDaysRemaining(expiration);
	}

	/**
	 * Sends the trail users expired email with subject and email body.
	 * 
	 * @param email
	 *            String
	 * @throws MessagingException
	 */

	private void sendTrialExpiredEmail(String email) throws MessagingException {
	//	emailService.send(email, trialExpiredSubject, null, trialExpiredEmailBody);
	}

}
