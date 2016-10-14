package com.wmsi.sgx.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Web Application configuration file
 */

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.wmsi.sgx.controller"})
@Import(value={AppConfig.class, DataConfig.class})
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jsonConverter());
    }
    
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}
	
    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() {

    	MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
    	jackson.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
    	jackson.setObjectMapper(objectMapper());
    	
    	return jackson;
    }

    @Bean
    public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return mapper;
    }

    /**
     * Catch all exceptions that might bubble up to prevent stack traces 
     * in the view. 
     */
    @Bean(name="simpleMappingExceptionResolver")
    public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();
        r.setDefaultErrorView("errors/error");
        return r;
    }

    @Bean
    public ViewResolver getViewResolver(){
        List<View> defaults = new ArrayList<View>();
        defaults.add(new MappingJackson2JsonView());

    	ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        resolver.setDefaultViews(defaults);
        return resolver;
    }

	@Bean	
	public RestTemplate restTemplate() {
	
		HttpClient httpClient = HttpClients.custom()
				.setConnectionManager(new PoolingHttpClientConnectionManager())
				.build();

		return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
	}    
}