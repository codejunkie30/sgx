package com.wmsi.sgx.util;

import javax.mail.MessagingException;

public interface EmailService{
	
	//Send Notification Email
	void send(String to, String subject, String body, String resetemailbody) throws MessagingException;
	//Send Notification Email
	void send(String to, String subject, String body) throws MessagingException;

}