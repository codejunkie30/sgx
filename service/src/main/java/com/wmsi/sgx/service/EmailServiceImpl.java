package com.wmsi.sgx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

	@Autowired
	private MailSender mailSender;
	
	@Override
	public void send(String to, String subject, String body){

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(to);
		msg.setFrom("fcdev@wealthmsi.com");
		msg.setReplyTo("fcdev@wealthmsi.com");
		msg.setSubject(subject);
		msg.setText(body);
		
		mailSender.send(msg);
	}

}
