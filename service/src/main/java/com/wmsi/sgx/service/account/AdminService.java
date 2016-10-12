package com.wmsi.sgx.service.account;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.account.AdminAccountModel;
import com.wmsi.sgx.model.account.AdminResponse;
import com.wmsi.sgx.model.account.TrialResponse;

/**
 * Create/deactivate the account and extend the trail/premium accounts. Search
 * with date by using the username or create date.Retrive the list of accounts
 * and logins and expiration dates.
 *
 */

public interface AdminService {

	/**
	 * Deactivates the account.
	 * 
	 * @param username
	 *            String, updatedBy long.
	 * 
	 * @return response
	 */
	AdminResponse deactivate(String username, long updatedBy);

	/**
	 * Searches with date by using the username or create date .
	 * 
	 * @param period
	 *            Date.
	 * 
	 * @return response
	 */
	
	AdminResponse searchByDate(Date period);

	/**
	 * Sets the expiration date extention.
	 * 
	 * @param username
	 *            String, updatedBy long, period Date
	 * 
	 * @return response
	 */
	
	AdminResponse extension(String username, Date period, long updatedBy);

	/**
	 * Activates/adds the account.
	 * 
	 * @param username
	 *            String, updatedBy long
	 * 
	 * @return response
	 */
	
	AdminResponse setAdmin(String username, long updatedBy);
	/**
   * This method find the users.
   * 
   * @param user String.
   * 
   * @return response
   */
	
	AdminResponse findByUser(String user);
	
	/**
	 * Deactivates/removes the account.
	 * 
	 * @param username
	 *            String, updatedBy long
	 * 
	 * @return response
	 */
	AdminResponse removeAdmin(String username, long updatedBy);

	/**
	 * Sets the full/halfway trail days.
	 * 
	 * @param username
	 *            String, response TrialResponse
	 * 
	 * @return response
	 */
	AdminResponse trialDay(TrialResponse response, String email);
	AdminResponse getTrialDays();

	/**
	 * Converts from AccountModel to AdminAccount .
	 * 
	 * @param accModel
	 *            AccountModel.
	 * 
	 * @return model
	 */
	AdminAccountModel convertAccountModelToAdminAccountModel(AccountModel accModel);
	
	/**
	 * Writes the into csv file.
	 * 
	 * @param name
	 *            String, header String
	 * 
	 * @response response HttpServletResponse
	 * 
	 * @throws IOException
	 */
	void writeCsv(HttpServletResponse response, String[] header, String name)
			throws IOException;
	
}
