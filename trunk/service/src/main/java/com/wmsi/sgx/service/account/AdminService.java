package com.wmsi.sgx.service.account;

import java.util.Date;

import com.wmsi.sgx.model.account.AdminResponse;

public interface AdminService {

	//Trial Days
	AdminResponse trialDay();
	AdminResponse deactivate(String username);
	AdminResponse searchByDate(Date period);
	AdminResponse extension(String username, Date period);
	AdminResponse setAdmin(String username);
	
}
