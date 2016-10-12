package com.wmsi.sgx.service.account;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.UpdateAccountModel;
import com.wmsi.sgx.model.account.AccountModel;

/**
 * Create trial/premium account and verifies the trail/premium period after
 * expiry the accounts for the users. Update accounts with the currency.
 *
 */

public interface AccountService{

	/**
	 * Retrieves the account details for the user name and set the currency and
	 * days remaining.
	 * 
	 * @param username
	 *            String
	 * 
	 * @return AccountModel
	 */
  
	AccountModel getAccountForUsername(String username);

	/**
	 * Creates PremiumAccount.
	 * 
	 * @param user
	 *            User
	 * @return AccountModel
	 * @throws Account
	 */
	Account createTrialAccount(User user) throws AccountCreationException;

	/**
	 * Creates PremiumAccount.
	 * 
	 * @param user
	 *            User
	 * @return true or false
	 */
	Account createPremiumAccount(User user);
	
	/**
	 * Deactivates the trial accounts or PREMIUM accounts.
	 * 
	 * @param user
	 *            User
	 * @return success
	 */
	Boolean convertToExpiry(User user);
	
	/**
	 * Updates account with the Currency and ContactOptIn.
	 * 
	 * @param updatedBy
	 *            long, dto UpdateAccountModel
	 * 
	 * @return AccountModel
	 */
	AccountModel updateAccount(UpdateAccountModel dto, long updatedBy);
	
	/**
	 * Checks the is PremiumUser or not.
	 * 
	 * @param u
	 *            User
	 * @return account
	 */
	Boolean isPremiumUser(User u);
	
	User findUserForTransactionId(String transId);
	
	Boolean setCurrencyIndex(String curr);

}