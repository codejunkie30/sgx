package com.wmsi.sgx.service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.wmsi.sgx.config.AppConfig.TrialProperty;

@Service
public class EmailServiceImpl implements EmailService{
	
	private Logger log = LoggerFactory.getLogger(EmailService.class);
	
	@Autowired
	private JavaMailSender mailSender;
	@Autowired 
	private TemplateEngine templateEngine;
	
	@Value ("${email.base}")
	public String baseUrl;
	
	@Value ("${email.sender}")
	public String emailSender;
	
	@Value ("${email.site.base}")
	public String emailSite;
	
	@Autowired
	private TrialProperty getTrial;
	
	@Override
	public void send(String to, String subject, String token, String file) throws MessagingException{
		
		final Context ctx = new Context();
		ctx.setVariable("token", token);
		ctx.setVariable("baseUrl", baseUrl);
		ctx.setVariable("emailSite", emailSite);
		ctx.setVariable("halfway", getTrial.getHalfwayDays());
		
		final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject(subject);
        try { message.setFrom(new InternetAddress(emailSender, "SGX")); }
        catch(Exception e) { log.error("Trying to create address", e); }
        message.setTo(to);
        
        final String htmlContent = templateEngine.process(file, ctx);
        
        message.setText(htmlContent, true /* isHtml */);
		
        mailSender.send(mimeMessage);
	}

	@Override
	public void send(String to, String subject, String body) throws MessagingException {
		final MimeMessage mimeMessage = mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
		message.setSubject(subject);
		try {
			message.setFrom(new InternetAddress(emailSender, "SGX"));
		} catch (Exception e) {
			log.error("Trying to create address", e);
		}
		message.setTo(to);
		message.setText(body, false /* isHtml */);

		mailSender.send(mimeMessage);
		
	}

}
