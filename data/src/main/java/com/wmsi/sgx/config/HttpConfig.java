package com.wmsi.sgx.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.service.indexer.IndexBuilderService;
import com.wmsi.sgx.service.indexer.IndexerService;
import com.wmsi.sgx.service.indexer.impl.IndexBuilderServiceImpl;
import com.wmsi.sgx.service.indexer.impl.IndexerServiceImpl;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.impl.CompanyResponseParser;
import com.wmsi.sgx.service.sandp.capiq.impl.CompanyService;
import com.wmsi.sgx.service.sandp.capiq.impl.FinancialsResponseParser;
import com.wmsi.sgx.service.sandp.capiq.impl.FinancialsService;
import com.wmsi.sgx.service.sandp.capiq.impl.HistoricalService;
import com.wmsi.sgx.service.sandp.capiq.impl.HoldersResponseParser;
import com.wmsi.sgx.service.sandp.capiq.impl.HoldersService;
import com.wmsi.sgx.service.sandp.capiq.impl.KeyDevResponseParser;
import com.wmsi.sgx.service.sandp.capiq.impl.KeyDevsService;

@Configuration
@ComponentScan(basePackages="com.wmsi.sgx.service")
@PropertySources(value = {
	@PropertySource("classpath:META-INF/properties/capiq.properties")
})
@Import(AppConfig.class)
public class HttpConfig{
	
	@Autowired
	public Environment capIQEnv;

    @Bean(name="capIqJsonMessageConverter")
    public MappingJackson2HttpMessageConverter capIqJsonConverter() {
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    	MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
    	jackson.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
    	jackson.setObjectMapper(mapper);
    	
    	return jackson;
    }
    
	@Bean(name = "capIqRestTemplate")	
	public RestTemplate capIqRestTemplate() {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(
						capIQEnv.getProperty("capiq.api.username"),
						capIQEnv.getProperty("capiq.api.password")));
		
		HttpClient httpClient = HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider)
				.setConnectionManager(new PoolingHttpClientConnectionManager())
				.setRetryHandler(new DefaultHttpRequestRetryHandler())
				.build();
		
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new FormHttpMessageConverter());
		converters.add(capIqJsonConverter());
		template.setMessageConverters(converters);
		
		return template;
	}
	
	@Bean
	public CapIQRequestExecutor capIqRequestExecutor(){
		CapIQRequestExecutor executor = new CapIQRequestExecutor();
		executor.setRestTemplate(capIqRestTemplate());
		executor.setUrl(capIQEnv.getProperty("capiq.api.url"));
		executor.setUserId(capIQEnv.getProperty("capiq.api.username"));
		return executor;
	}

	@Bean(name="esObjectMapper")
	public ObjectMapper esObjectMapper(){
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	return mapper;
	}
	
    @Bean(name="esJsonConverter")
    public MappingJackson2HttpMessageConverter esJsonConverter() {
    	MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
    	jackson.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
    	jackson.setObjectMapper(esObjectMapper());
    	
    	return jackson;
    }
    
	@Bean(name = "esRestTemplate")	
	public RestTemplate esRestTemplate() {
		HttpClient httpClient = HttpClients.custom()
				.setConnectionManager(new PoolingHttpClientConnectionManager())
				.build();
		
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(esJsonConverter());		
		template.setMessageConverters(converters);
		
		return template;
	}
	
	@Bean
	public DefaultFtpSessionFactory ftpSessionFactory(){
		DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
		factory.setClientMode(2);
		factory.setHost(capIQEnv.getProperty("capiq.ftp.url"));
		factory.setPort(Integer.valueOf(capIQEnv.getProperty("capiq.ftp.port")));
		factory.setUsername(capIQEnv.getProperty("capiq.ftp.user"));
		factory.setPassword(capIQEnv.getProperty("capiq.ftp.pass"));		
		return factory;
	}
	
	@Bean
	public IndexBuilderService indexBuilderService(){		
		return new IndexBuilderServiceImpl();
	}
	
	@Bean
	public IndexerService indexerService(){
		IndexerServiceImpl service = new IndexerServiceImpl();
		service.setRestTemplate(esRestTemplate());
		return service;
	}
	
	@Bean
	public CompanyService companyService(){
		CompanyService service = new CompanyService();
		service.setRequestExecutor(capIqRequestExecutor());
		service.setResponseParser(new CompanyResponseParser());
		return service;
	}

	@Bean
	public FinancialsService financialsService(){
		FinancialsService  service = new FinancialsService();
		service.setRequestExecutor(capIqRequestExecutor());
		service.setResponseParser(new FinancialsResponseParser());
		return service;
	}

	@Bean
	public HistoricalService historicalService(){
		HistoricalService service = new HistoricalService();
		service.setRequestExecutor(capIqRequestExecutor());
		return service;
	}

	@Bean
	public KeyDevsService keyDevsService(){
		KeyDevsService service = new KeyDevsService();
		service.setRequestExecutor(capIqRequestExecutor());
		service.setResponseParser(new KeyDevResponseParser());
		return service;
	}

	@Bean
	public HoldersService holdersService(){
		HoldersService service = new HoldersService ();
		service.setRequestExecutor(capIqRequestExecutor());
		service.setResponseParser(new HoldersResponseParser());
		return service;
	}

}
