package com.wmsi.sgx.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.wmsi.sgx.controller"})
@EnableTransactionManagement
public class WebAppConfig extends WebMvcConfigurationSupport {

	@Bean
	public DomainClassConverter<?> domainClassConverter() {
		return new DomainClassConverter<FormattingConversionService>(mvcConversionService());
	}

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
    	jackson.setObjectMapper(objectMapper);
    	
    	return jackson;
    }

    @Autowired
    public ObjectMapper objectMapper;

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
        resolver.setViewResolvers(null);
        return resolver;
    }

    @Autowired
    private Environment env;
    
}