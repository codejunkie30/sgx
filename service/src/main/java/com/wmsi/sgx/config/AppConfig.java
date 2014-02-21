package com.wmsi.sgx.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan(basePackages = {"com.wmsi.sgx.service"})
public class AppConfig {

	@Bean
	public static PropertyPlaceholderConfigurer properties(){
	   PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	   Resource[] resources = new ClassPathResource[ ]
	      { new ClassPathResource( "META-INF/properties/application.properties" ) };
	   ppc.setLocations( resources );
	   ppc.setIgnoreUnresolvablePlaceholders( true );
	   return ppc;
	}
}
