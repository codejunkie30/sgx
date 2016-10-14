package com.wmsi.sgx.util;

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
	
	/**
	 * Send Notification Email 
	 * @param sender
	 * @param reciever
	 * @param subject
	 * @param file
	 * @return 
	 * @throws MessagingException
	 */
	@Override
	public void send(String to, String subject, String token, String file) throws MessagingException{
		
		final Context ctx = new Context();
		ctx.setVariable("token", token);
		ctx.setVariable("baseUrl", baseUrl);
		ctx.setVariable("emailSite", emailSite);
		
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
	/**
	 * Send Notification Email 
	 * @param sender
	 * @param subject
	 * @param body
	 * @return 
	 * @throws MessagingException
	 */
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
