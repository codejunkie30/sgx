package com.wmsi.sgx.util;

import javax.mail.MessagingException;

public interface EmailService{

	void send(String to, String subject, String body, String resetemailbody) throws MessagingException;
	void send(String to, String subject, String body) throws MessagingException;

}