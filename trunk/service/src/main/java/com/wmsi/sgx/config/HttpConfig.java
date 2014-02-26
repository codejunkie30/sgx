package com.wmsi.sgx.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class HttpConfig{

    @Bean(name="capIqJsonMessageConverter")
    public MappingJackson2HttpMessageConverter capIqJsonConverter() {
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	
    	mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);

    	MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
    	jackson.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
    	jackson.setObjectMapper(mapper);
    	
    	return jackson;
    }
    
	@Bean(name = "capIqRestTemplate")
	public RestTemplate restTemplate() {

		String user = "APIADMIN@WEALTHMSI.COM";
		String password = "fr3xuhEs";
		
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(user, password));
		
		HttpClient httpClient = HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider)
				.setConnectionManager(connManager)
				.build();
		
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		
		RestTemplate template = new RestTemplate(requestFactory);

		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new FormHttpMessageConverter());
		converters.add(capIqJsonConverter());

		template.setMessageConverters(converters);
		return template;
	}

}
