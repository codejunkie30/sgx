package com.wmsi.sgx.service.account;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


public interface AcccountExiprationService {

	public void checkAccountExpiration();
}
