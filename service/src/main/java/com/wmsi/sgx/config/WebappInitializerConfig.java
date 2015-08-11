package com.wmsi.sgx.config;

import javax.servlet.Filter;

import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.wmsi.sgx.web.filter.JsonpWrappingFilter;

@Order(1)
public class WebappInitializerConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[]{AppConfig.class, DataConfig.class, SearchConfig.class, SecurityConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[]{};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}

	@Override
	protected Filter[] getServletFilters(){
		return new Filter[]{new JsonpWrappingFilter()};
	}
}
