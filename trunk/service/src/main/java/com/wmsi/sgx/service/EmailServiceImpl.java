package com.wmsi.sgx.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService{
	
	@Autowired
	private JavaMailSender mailSender;
	@Autowired 
	private TemplateEngine templateEngine;
	
	@Override
	public void send(String to, String subject, String token, String file) throws MessagingException{
		
		final Context ctx = new Context();
		ctx.setVariable("token", token);
		
		final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject(subject);
        message.setFrom("fcdev@wealthmsi.com");
        message.setTo(to);
        
        final String htmlContent = templateEngine.process(file, ctx);
        
        message.setText(htmlContent, true /* isHtml */);
		
        mailSender.send(mimeMessage);
	}

}
