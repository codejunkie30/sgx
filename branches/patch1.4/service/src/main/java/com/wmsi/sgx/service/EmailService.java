package com.wmsi.sgx.service;

import javax.mail.MessagingException;

public interface EmailService{

	void send(String to, String subject, String body, String resetemailbody) throws MessagingException;

}