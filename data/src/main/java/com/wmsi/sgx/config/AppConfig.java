package com.wmsi.sgx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

@Configuration
@PropertySources(value = {
		// If spring.profiles is set use <profile>.application.properties else defaults to application.properties
		// Uses -Dconfig.file to override internal settings with external file, must prefix path with file://
		@PropertySource(value="classpath:META-INF/properties/application.properties"),
		@PropertySource(value="classpath:META-INF/properties/${spring.profiles.active:dummy}.application.properties"),
		@PropertySource(value="${config.file:classpath:META-INF/properties/dummy.application.properties}", ignoreResourceNotFound=false)
	})
public class AppConfig{

	@Autowired
	public Environment env;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
		PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
		ppc.setIgnoreResourceNotFound(true);
		ppc.setIgnoreUnresolvablePlaceholders(true);
		return ppc;
	}
	
	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(env.getProperty("mail.host"));
		sender.setUsername(env.getProperty("mail.user"));
		sender.setPassword(env.getProperty("mail.password"));
		return sender;
	}

	@Bean
	public UrlTemplateResolver emailTemplateResolver() {
		UrlTemplateResolver emailTemplateResolver = new UrlTemplateResolver();
		emailTemplateResolver.setTemplateMode("HTML5");
		emailTemplateResolver.setCharacterEncoding("UTF-8");
		emailTemplateResolver.setOrder(1);
/*		emailTemplateResolver.setCacheable(cachable);
		if (cachable) {
			emailTemplateResolver.setCacheTTLMs(cacheDuration);
		}
*/
		return emailTemplateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(this.emailTemplateResolver());
		return engine;
	}

	
}
