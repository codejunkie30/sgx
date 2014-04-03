package com.wmsi.sgx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.impl.SearchServiceImpl;

@Configuration
public class SearchConfig{
	
	@Value("${elasticsearch.index.name}")
	private String indexName;
	
	@Bean
	public SearchService companySearchService(){
		SearchServiceImpl serv = new SearchServiceImpl();
		serv.setIndexName(indexName);
		serv.setType("company");
		return serv;
	}
	
	@Bean
	public SearchService alphaFactorSearchService(){
		SearchServiceImpl serv = new SearchServiceImpl();
		serv.setIndexName(indexName);
		serv.setType("alphaFactor");
		return serv;
	}
}
