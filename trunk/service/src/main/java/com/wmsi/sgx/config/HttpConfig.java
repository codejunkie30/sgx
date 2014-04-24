package com.wmsi.sgx.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.service.search.elasticsearch.ESQueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;

@Configuration
public class HttpConfig{

	@Bean 
	public ElasticSearchService elasticSearchService(){
		ElasticSearchService es = new ElasticSearchService();
		es.setExecutor(esQueryExecutor());
		es.setMapper(esObjectMapper());
		return es;
	}
	
	@Bean
	public ESQueryExecutor esQueryExecutor(){
		ESQueryExecutor executor = new ESQueryExecutor();
		executor.setRestTemplate(esRestTemplate());
		return executor;
	}
	
	@Bean(name="esObjectMapper")
	public ObjectMapper esObjectMapper(){
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
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
}
