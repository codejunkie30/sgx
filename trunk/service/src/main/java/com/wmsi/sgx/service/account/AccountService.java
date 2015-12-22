package com.wmsi.sgx.service.account;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.UpdateAccountModel;
import com.wmsi.sgx.model.account.AccountModel;

public interface AccountService{

	AccountModel getAccountForUsername(String username);

	Account createTrialAccount(User user) throws AccountCreationException;

	Account createPremiumAccount(User user);
	
	Boolean convertToExpiry(User user);
	
	AccountModel updateAccount(UpdateAccountModel dto);
	
	Boolean isPremiumUser(User u);

}