package com.wmsi.sgx.model.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Payload;

public class IntegrationLoggerAdapter{

	private static final Logger log = LoggerFactory.getLogger(IntegrationLoggerAdapter.class);

	public void info(@Payload Object msg) {
		if(msg instanceof Throwable)
			log.info(msg.toString(), msg);
		else
			log.info(msg.toString());		
	}

	public void debug(@Payload Object msg) {
		if(msg instanceof Throwable)
			log.debug(msg.toString(), msg);
		else
			log.debug(msg.toString());
	}

	public void error(@Payload Object msg) {
		if(msg instanceof Throwable)
			log.error(msg.toString(), msg);
		else
			log.error(msg.toString());
	}

}
