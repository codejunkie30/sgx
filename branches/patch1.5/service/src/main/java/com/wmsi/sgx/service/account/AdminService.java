package com.wmsi.sgx.service.account;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.account.AdminAccountModel;
import com.wmsi.sgx.model.account.AdminResponse;
import com.wmsi.sgx.model.account.TrialResponse;

public interface AdminService {

	//Trial Days
	AdminResponse deactivate(String username);
	AdminResponse searchByDate(Date period);
	AdminResponse extension(String username, Date period);
	AdminResponse setAdmin(String username);
	AdminResponse findByUser(String user);
	AdminResponse removeAdmin(String username);
	AdminResponse trialDay(TrialResponse response, String email);
	AdminResponse getTrialDays();
	AdminAccountModel convertAccountModelToAdminAccountModel(AccountModel accModel);
	void writeCsv(HttpServletResponse response, String[] header, String name)
			throws IOException;
	
}
