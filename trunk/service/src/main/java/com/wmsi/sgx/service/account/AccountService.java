package com.wmsi.sgx.service.account;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.UpdateAccountModel;
import com.wmsi.sgx.model.account.AccountModel;

/**
 * 
 * The AccountService handles operations related to Account 
 *
 */
public interface AccountService{

	/**
	 * Retrieves the account details for the user name
	 * 
	 * @param username
	 * @return
	 */
	AccountModel getAccountForUsername(String username);

	/**
	 * Creates Trial Account for the given user information
	 * 
	 * @param user
	 * @return Account
	 * @throws AccountCreationException
	 */
	Account createTrialAccount(User user) throws AccountCreationException;

	/**
	 * Creates Premium account for the given user information
	 * 
	 * @param user User
	 * @return Account
	 */
	Account createPremiumAccount(User user);
	
	/**
	 * Converts the trial Premium accounts to expired accounts based on the user
	 * information provided.
	 * 
	 * @param user
	 *            User
	 * @return true or false
	 */
	Boolean convertToExpiry(User user);
	
	/**
	 * Updates account with the Currency and ContactOptIn.
	 * 
	 * @param dto
	 *            UpdateAccountModel
	 * @param updatedBy
	 * @return AccountModel
	 */
	AccountModel updateAccount(UpdateAccountModel dto, long updatedBy);
	
	/**
	 * Checks if the user is a Premium user
	 * 
	 * @param u
	 *            User
	 * @return Returns true if the user is premium
	 */
	Boolean isPremiumUser(User u);
	
	/**
	 * Retrieves the user based on the transaction id
	 * 
	 * @param transId
	 * @return User
	 */
	User findUserForTransactionId(String transId);
	
	/**
	 * Sets the currency index
	 * 
	 * @param curr
	 *            Currency index
	 * @return true or false
	 */
	Boolean setCurrencyIndex(String curr);

}