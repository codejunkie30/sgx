package com.wmsi.sgx.config;

import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;


@Configuration
@ComponentScan(basePackages = { "com.wmsi.sgx.service" })
@EnableCaching
@EnableScheduling
@PropertySources(value = {
		// If spring.profiles is set use <profile>.application.properties else defaults to application.properties
		// Uses -Dconfig.file to override internal settings with external file, must prefix path with file://
		@PropertySource(value="classpath:META-INF/properties/application.properties"),
		@PropertySource(value="classpath:META-INF/properties/${spring.profiles.active:dummy}.application.properties"),
		@PropertySource(value="${config.file:classpath:META-INF/properties/dummy.application.properties}", ignoreResourceNotFound=false)
	})
@Import(value = {WebAppConfig.class, DataConfig.class, SearchConfig.class, SecurityConfig.class })
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
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
		PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
		ppc.setIgnoreResourceNotFound(true);
		ppc.setIgnoreUnresolvablePlaceholders(true);
		return ppc;
	}
	
	@Bean
	public DozerBeanMapperFactoryBean dozerMappingBean() throws Exception{

		DozerBeanMapperFactoryBean factory = new DozerBeanMapperFactoryBean();
		factory.setMappingFiles( 
				new PathMatchingResourcePatternResolver()
					.getResources("classpath:META-INF/mappings/dozer/*.xml"));
		
		return factory; 
	}
	
    @Bean
    public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return mapper;
    }

	@Bean
	public JavaMailSender mailSender(){
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(env.getProperty("mail.host"));
		sender.setUsername(env.getProperty("mail.user"));
		sender.setPassword(env.getProperty("mail.password"));
		return sender;
	}
	
	@Value ("${email.cachable}")
	Boolean cachable;
	
	@Value ("${email.cachable.duration}")
	Long cacheDuration;
	
	@Bean
	public UrlTemplateResolver emailTemplateResolver(){
		UrlTemplateResolver emailTemplateResolver = new UrlTemplateResolver();
        emailTemplateResolver.setTemplateMode("HTML5");
        emailTemplateResolver.setCharacterEncoding("UTF-8");
        emailTemplateResolver.setOrder(1);
        emailTemplateResolver.setCacheable(cachable);
        if(cachable){
        	emailTemplateResolver.setCacheTTLMs(cacheDuration);
        }
        
        return emailTemplateResolver;
	}
	
	@Bean
	public SpringTemplateEngine templateEngine(){
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(this.emailTemplateResolver());
		return engine;
	}

}
