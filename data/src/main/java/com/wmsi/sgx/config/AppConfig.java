package com.wmsi.sgx.config;

import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

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
	
	/*
	@Bean
	public DozerBeanMapperFactoryBean dozerMappingBean() throws Exception{

		DozerBeanMapperFactoryBean factory = new DozerBeanMapperFactoryBean();
		factory.setMappingFiles( 
				new PathMatchingResourcePatternResolver()
					.getResources("classpath*:META-INF/mappings/dozer/*.xml"));
		
		return factory; 
	}
	*/
}
