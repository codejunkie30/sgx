package com.wmsi.sgx.service;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
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

import com.wmsi.sgx.model.AlertOption;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.model.search.CompanyPrice;

@Service
public class WatchlistSenderServiceImpl implements WatchlistSenderService{
	
	@Autowired
	private JavaMailSender mailSender;
	@Autowired 
	private TemplateEngine templateEngine;
	
	
	@Value ("${email.base}")
	public String baseUrl;
	@Value ("${email.site.base}")
	public String emailSite;
	@Value ("${email.sender}")
	public String emailSender;
	@Value ("${email.watchlist.alert}")
	public String file;
	private Logger log = LoggerFactory.getLogger(WatchlistSenderService.class);
	
	@Override
	public void send(String to, String subject,	List<AlertOption> variables, WatchlistModel watchlist, List<CompanyPrice> companyPrices) throws MessagingException {
				
		final Context ctx = new Context();		
		ctx.setVariable("watchlistName", watchlist.getName());
		ctx.setVariable("companyPrices", companyPrices);			
		ctx.setVariable("alerts", variables);
		ctx.setVariable("baseUrl", baseUrl);
		ctx.setVariable("emailSite", emailSite);		

		final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject(subject);
        message.setFrom(emailSender);
        message.setTo(to);        
        final String htmlContent = templateEngine.process(file, ctx);        
        message.setText(htmlContent, true /* isHtml */);		
        mailSender.send(mimeMessage);
		
        log.info("Watchlist Email sent to: {} for watchList: {}", to, watchlist.getName());
        log.info(" Email information \n " +ctx.getVariables().get("watchlistName"));
        log.info(" Email information \n " +ctx.getVariables().get("companyPrices"));
        log.info(" Email information \n " +ctx.getVariables().get("alerts"));
	}

}
