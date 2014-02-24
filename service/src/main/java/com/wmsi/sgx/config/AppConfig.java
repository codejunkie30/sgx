package com.wmsi.sgx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.wmsi.sgx.service.quanthouse.feedos.FeedOSConfig;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSSession;

@Configuration
@ComponentScan(basePackages = { "com.wmsi.sgx.service" })
@EnableCaching
@PropertySource("classpath:META-INF/properties/application.properties")
public class AppConfig{

	@Autowired
	public Environment env;

	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
		EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource(
				"META-INF/cache/ehcache.xml"));
		return ehCacheManagerFactoryBean;
	}

	@Bean
	public CacheManager cacheManager() {
		EhCacheCacheManager cacheManager = new EhCacheCacheManager();
		cacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
		return cacheManager;
	}
	
	@Bean
	public FeedOSSession feedOSSession(){
		
		FeedOSConfig config = new FeedOSConfig();
		config.setSessionName(env.getProperty("quanthouse.api.sessionName"));
		config.setUrl(env.getProperty("quanthouse.api.url"));
		config.setPort(env.getProperty("quanthouse.api.port", Integer.class));
		config.setUser(env.getProperty("quanthouse.api.user"));
		config.setPassword(env.getProperty("quanthouse.api.password"));
		
		FeedOSSession session = new FeedOSSession();
		session.setFeedOSconfig(config);
		return session;
	}

	/*
	@Bean
	public static PropertyPlaceholderConfigurer properties() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		Resource[] resources = new ClassPathResource[] { new ClassPathResource(
				"META-INF/properties/application.properties") };
		ppc.setLocations(resources);
		ppc.setIgnoreUnresolvablePlaceholders(true);
		return ppc;
	}*/

}
