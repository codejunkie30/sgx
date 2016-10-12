package com.wmsi.sgx.service.account;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.account.AdminAccountModel;
import com.wmsi.sgx.model.account.AdminResponse;
import com.wmsi.sgx.model.account.TrialResponse;

/**
 * The AdminService handles operations like creating the account, deactivate the
 * account, extend the trail/premium accounts and other admin operations
 *
 */
public interface AdminService {

	/**
	 * Deactivates the account.
	 * 
	 * @param username
	 *            User name
	 * @param updatedBy
	 *            Updated by user name
	 * @return AdminResponse
	 */
	AdminResponse deactivate(String username, long updatedBy);

	/**
	 * Retrieves accounts filtered on the start date provided
	 * 
	 * @param period Start date period
	 * @return AdminResponse
	 */
	AdminResponse searchByDate(Date period);

	/**
	 * Extends the expiration date for the user with the date provided
	 * 
	 * @param username
	 *            User name
	 * @param period
	 *            Expiration date
	 * @param updatedBy
	 *            Updated by user name
	 * @return AdminResponse
	 */
	AdminResponse extension(String username, Date period, long updatedBy);

	/**
	 * Converts the user as admin for the user name provided
	 * 
	 * @param username
	 *            User name
	 * @param updatedBy
	 *            Update by user name
	 * @return AdminResponse
	 */
	AdminResponse setAdmin(String username, long updatedBy);
	
	/**
	 * Retrieves the user based on the user name
	 * 
	 * @param user
	 *            User name
	 * @return AdminResponse
	 */
	AdminResponse findByUser(String user);
	
	/**
	 * Removes admin access for the user name provided
	 * 
	 * @param username
	 *            User name
	 * @param updatedBy
	 *            Update by user name
	 * @return AdminResponse
	 */
	AdminResponse removeAdmin(String username, long updatedBy);

	/**
	 * Sets the full/halfway trail days.
	 * 
	 * @param response
	 *            TrialResponse
	 * @param email
	 *            User name
	 * @return AdminResponse
	 */
	AdminResponse trialDay(TrialResponse response, String email);
	
	/**
	 * Retrieves the trail days
	 * 
	 * @return AdminResponse
	 */
	AdminResponse getTrialDays();

	/**
	 * Converts the AccountModel to AdminAccountModel .
	 * 
	 * @param accModel AccountModel
	 * @return AdminAccountModel
	 */
	AdminAccountModel convertAccountModelToAdminAccountModel(AccountModel accModel);
	
	/**
	 * Returns a csv file with the list of user account information
	 * 
	 * @param response HttpServletResponse
	 * @param header
	 * @param name file name
	 * @throws IOException
	 */
	void writeCsv(HttpServletResponse response, String[] header, String name)
			throws IOException;
	
}
