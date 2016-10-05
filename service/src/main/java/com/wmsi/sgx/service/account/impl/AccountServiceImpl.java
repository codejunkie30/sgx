package com.wmsi.sgx.service.account.impl;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.config.AppConfig.TrialProperty;
import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.domain.EnetsTransactionDetails;
import com.wmsi.sgx.domain.SortAccountByExpirationDateComparator;
import com.wmsi.sgx.domain.SortDatesDecendingEnetsTransactionId;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.UpdateAccountModel;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.repository.EnetsRepository;
import com.wmsi.sgx.service.account.AccountCreationException;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;
import com.wmsi.sgx.util.DateUtil;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private EnetsRepository enetsRepository;

	@Autowired
	private TrialProperty getTrial;

	@Autowired
	private ElasticSearchService esSearchService;

	private static final int PREMIUM_EXPIRATION_DAYS = 365;

	/*
	 * TODO Add constraint to account table for unique user, accountType,
	 * active? Add unique constraint user/active Add method to repo for
	 * findByUserTypeActive
	 */

	@Override
	// @Secured("ROLE_USER")
	public AccountModel getAccountForUsername(String username) {

		// TODO, ensure there is only ever one active account
		List<Account> accounts = accountRepository.findByUsername(username);
		AccountModel ret = null;
		Collections.sort(accounts, new SortAccountByExpirationDateComparator());
		Account account;
		if (accounts.size() > 0) {
			account = accounts.get(0);
			ret = new AccountModel();
			ret.setEmail(account.getUser().getUsername());
			ret.setStartDate(DateUtil.resetTimeStamp(account.getStartDate()));
			if (account.getExpirationDate() != null) {
				ret.setExpirationDate(DateUtil.resetTimeStamp(account.getExpirationDate()));

			} else {
				Date expiration = DateUtil.toDate(DateUtil.adjustDate(DateUtil.fromDate(account.getStartDate()),
						Calendar.DAY_OF_MONTH,
						account.getType() == AccountType.TRIAL ? getTrial.getTrialDays() : PREMIUM_EXPIRATION_DAYS));
				ret.setExpirationDate(DateUtil.resetTimeStamp(expiration));
			}
			if (account.getActive().equals(false)) {
				ret.setType(AccountType.EXPIRED);
			} else {
				ret.setType(account.getType());
			}
			ret.setDaysRemaining(DateUtil.getDaysRemaining(ret.getExpirationDate()));
			ret.setContactOptIn(account.getContactOptIn());
			ret.setCurrency(account.getCurrency());
			if (account.getType() == AccountType.PREMIUM || account.getType() == AccountType.ADMIN
					|| account.getType() == AccountType.MASTER) {
				/*
				 * EnetsTransactionDetails enetsTrasactionDetail =
				 * enetsRepository.findByUserAndActive(account.getActive(),
				 * account.getUser().getId()); if(enetsTrasactionDetail !=
				 * null){
				 * ret.setEnetsTranId((enetsTrasactionDetail.getTrans_id())); }
				 */
				List<EnetsTransactionDetails> enetsTrasactionDetail = enetsRepository
						.findByUserAndActive(account.getActive(), account.getUser().getId());
				if (enetsTrasactionDetail.size() != 0) {
					Collections.sort(enetsTrasactionDetail, new SortDatesDecendingEnetsTransactionId());
					ret.setEnetsTranId((enetsTrasactionDetail.get(0).getTrans_id()));
				}

			}
		}

		return ret;
	}

	@Override
	public User findUserForTransactionId(String transId) {
		return enetsRepository.findByTransactionId(transId);
	}

	public Boolean setCurrencyIndex(String esIndexName) {
		esSearchService.setIndexName(esIndexName);
		return false;
	}

	@Override
	// @Secured("ROLE_USER")
	public AccountModel updateAccount(UpdateAccountModel dto, long updatedBy) {

		List<Account> accounts = accountRepository.findByUsername(dto.getEmail());
		Collections.sort(accounts, new SortAccountByExpirationDateComparator());
		if (accounts.size() > 0) {
			accounts.get(0).setContactOptIn(dto.getContactOptIn());
			accounts.get(0).setCurrency(dto.getCurrency());
			accountRepository.accountserviceUpdateAccount(accounts.get(0).getContactOptIn(),
					accounts.get(0).getCurrency(), accounts.get(0).getUser().getId(), updatedBy, new Date());
		}
		return getAccountForUsername(dto.getEmail());

	}

	@Override
	public Account createTrialAccount(User user) throws AccountCreationException {

		List<Account> accounts = accountRepository.findAllByUser(user);

		// Sanity check, there shouldn't be any other accounts yet.
		if (accounts.size() > 0)
			throw new AccountCreationException();

		return createAccount(user, AccountType.TRIAL, getTrial.getTrialDays());

	}

	@Override
	public Account createPremiumAccount(User user) {

		List<Account> accounts = accountRepository.findAllByUser(user);

		for (Account acc : accounts) {

			// Deactivate trial and Premium accounts
			if (acc.getType().equals(AccountType.TRIAL) || acc.getType().equals(AccountType.PREMIUM)) {
				acc.setActive(false);
				accountRepository.updateActive(acc.getActive(), acc.getUser().getId(), acc.getUser().getId(),
						new Date());
			}
		}

		return createAccount(user, AccountType.PREMIUM, PREMIUM_EXPIRATION_DAYS);

	}

	@Override
	public Boolean isPremiumUser(User u) {
		if (u != null) {
			AccountModel accountModel = getAccountForUsername(u.getUsername());
			if (accountModel.getType() == AccountType.PREMIUM || accountModel.getType() == AccountType.TRIAL
					|| accountModel.getType() == AccountType.ADMIN || accountModel.getType() == AccountType.MASTER)
				return true;
		}
		return false;
	}

	@Override
	public Boolean convertToExpiry(User user) {
		Boolean success = false;
		List<Account> accounts = accountRepository.findAllByUser(user);

		for (Account acc : accounts) {

			// Deactivate trial account
			if (acc.getType().equals(AccountType.TRIAL) || (acc.getType().equals(AccountType.PREMIUM))) {
				acc.setActive(false);
				accountRepository.updateActive(acc.getActive(), acc.getUser().getId(), acc.getUser().getId(),
						new Date());
				success = true;
			}
		}

		return success;

	}

	private Account createAccount(User user, AccountType type, int expirationDays) {

		Account acc = new Account();
		acc.setType(type);
		acc.setUser(user);
		acc.setStartDate(new Date());
		if (type == AccountType.TRIAL)
			acc.setExpirationDate(null);
		else
			acc.setExpirationDate(DateUtil.resetTimeStamp(new DateTime().plusDays(expirationDays).toDate()));
		acc.setActive(true);
		acc.setAlwaysActive(false);
		acc.setContactOptIn(user.getContactOptIn());
		return accountRepository.save(acc);

	}
}
