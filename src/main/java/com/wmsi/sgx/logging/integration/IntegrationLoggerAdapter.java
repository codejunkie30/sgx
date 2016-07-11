package com.wmsi.sgx.logging.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Payload;

import com.wmsi.sgx.exception.ErrorBeanHelper;

public class IntegrationLoggerAdapter{

	@Autowired
	private ErrorBeanHelper errorBeanHelper;

	private static final Logger log = LoggerFactory.getLogger(IntegrationLoggerAdapter.class);

	public void info(@Payload Object msg) {
		if(msg instanceof Throwable){
			log.info(msg.toString(), msg);
			errorBeanHelper.sendEmail();
		}else
			log.info(msg.toString());		
	}

	public void debug(@Payload Object msg) {
		if(msg instanceof Throwable){
			log.debug(msg.toString(), msg);
			errorBeanHelper.sendEmail();
	}else
			log.debug(msg.toString());
	}

	public void error(@Payload Object msg) {
		if(msg instanceof Throwable){
			log.error(msg.toString(), msg);
			errorBeanHelper.sendEmail();
		}else
			log.error(msg.toString());
	}


}
