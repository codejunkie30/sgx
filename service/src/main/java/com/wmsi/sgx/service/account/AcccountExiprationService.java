package com.wmsi.sgx.service.account;

import javax.mail.MessagingException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


public interface AcccountExiprationService {

	void checkAccountExpiration() throws MessagingException;
	void sendAccountExpirationHalfWayEmail() throws MessagingException;
}
