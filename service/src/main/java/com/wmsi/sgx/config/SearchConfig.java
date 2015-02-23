package com.wmsi.sgx.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;
import com.wmsi.sgx.service.search.elasticsearch.QueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.impl.ESQueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.impl.ElasticSearchServiceImpl;
import com.wmsi.sgx.service.search.impl.SearchServiceImpl;

@Configuration
public class SearchConfig{

	@Value("${elasticsearch.index.name}")
	private String indexName;
	
	@Value("${elasticsearch.url}")
	private String indexUrl;

	private SearchService searchService(String type){
		SearchServiceImpl serv = new SearchServiceImpl();
		serv.setIndexName(indexName);
		serv.setType(type);
		return serv;
	}

	@Bean
	public SearchService companySearch(){
		return searchService("company");
	}
	
	@Bean
	public SearchService alphaFactorSearch(){
		return searchService("alphaFactor");
	}

	@Bean 
	public SearchService priceHistorySearch(){
		return searchService("price");		
	}
	
	@Bean 
	public SearchService highPriceHistorySearch(){
		return searchService("highPrice");		
	}
	
	@Bean 
	public SearchService lowPriceHistorySearch(){
		return searchService("lowPrice");		
	}
	
	@Bean 
	public SearchService openPriceHistorySearch(){
		return searchService("openPrice");		
	}

	@Bean
	public SearchService volumeHistorySearch(){
		return searchService("volume");
	}

	@Bean 
	public SearchService financialSearch(){
		return searchService("financial");
	}

	@Bean 
	public SearchService gtiSearch(){
		return searchService("gtis");
	}

	@Bean 
	public SearchService holdersSearch(){
		return searchService("holders");		
	}

	@Bean 
	public SearchService keyDevsSearch(){
		return searchService("keyDevs");		
	}
	
	@Bean 
	public ElasticSearchService elasticSearchService(){
		ElasticSearchServiceImpl es = new ElasticSearchServiceImpl();
		es.setExecutor(esQueryExecutor());
		es.setMapper(objectMapper);
		return es;
	}
	
	@Bean
	public QueryExecutor esQueryExecutor(){
		ESQueryExecutor executor = new ESQueryExecutor();
		executor.setRestTemplate(esRestTemplate());
		executor.setIndexUrl(indexUrl);
		return executor;
	}
	
	@Autowired 
	private MappingJackson2HttpMessageConverter jsonConverter;

	@Autowired 
	private ObjectMapper objectMapper;

	@Bean(name = "esRestTemplate")	
	public RestTemplate esRestTemplate() {
		HttpClient httpClient = HttpClients.custom()
				.setConnectionManager(new PoolingHttpClientConnectionManager())
				.build();

		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(jsonConverter);
		
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
		template.setMessageConverters(converters);
		
		return template;
	}
}
