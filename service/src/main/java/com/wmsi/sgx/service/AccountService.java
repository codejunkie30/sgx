package com.wmsi.sgx.service;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.dto.AccountModel;

public interface AccountService{

	AccountModel getAccountForUsername(String username);

	Account createTrialAccount(User user) throws AccountCreationException;

	Account createPremiumAccount(User user);

}