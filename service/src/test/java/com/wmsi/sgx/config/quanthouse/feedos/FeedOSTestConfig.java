package com.wmsi.sgx.config.quanthouse.feedos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import com.wmsi.sgx.config.TestConfig;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSConfig;

@Configuration
@Import(TestConfig.class)
public class FeedOSTestConfig{

	@Autowired
	public Environment env;
	
	@Bean
	public FeedOSConfig feedOSConfig(){
		FeedOSConfig config = new FeedOSConfig();
		config.setSessionName(env.getProperty("quanthouse.api.sessionName"));
		config.setUrl(env.getProperty("quanthouse.api.url"));
		config.setPort(env.getProperty("quanthouse.api.port", Integer.class));
		config.setUser(env.getProperty("quanthouse.api.user"));
		config.setPassword(env.getProperty("quanthouse.api.password"));
		return config;
	}
	
}
