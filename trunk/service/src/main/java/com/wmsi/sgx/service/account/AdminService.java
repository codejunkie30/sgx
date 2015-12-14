package com.wmsi.sgx.service.account;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
	AdminResponse trialDay(TrialResponse response);
	AdminResponse getTrialDays();
	void writeCsv(HttpServletResponse response, String[] header, String name)
			throws IOException;
	
}
