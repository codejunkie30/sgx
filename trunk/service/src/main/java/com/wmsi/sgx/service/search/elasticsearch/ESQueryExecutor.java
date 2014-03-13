package com.wmsi.sgx.service.search.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.wmsi.sgx.model.CompanyInfo;

@Service
public class ESQueryExecutor{
	
	private static final Logger log = LoggerFactory.getLogger(ESQueryExecutor.class);
	
	@Autowired
	@Qualifier("esRestTemplate")
	private RestTemplate elasticSearchRestTemplate;
	
	@Value("${elasticsearch.url}")
	private String indexUrl;

	public ESResponse executeQuery(ESQuery q) throws ElasticSearchException{
		
		try{
			log.debug("Executing query on {}", indexUrl);
			
			JsonNode query = q.toJson();
			
			log.trace("Query: {}", query);
			String url = indexUrl.concat(q.getURI().toString());
			
			JsonNode res = elasticSearchRestTemplate.postForObject(url, query, JsonNode.class);
			
			log.debug("Query returned successfully");
			log.trace("ES Response: {}", res);
			
			return new ESResponse(res);
		}
		catch(RestClientException e){
			throw new ElasticSearchException("Rest exception executing query", e);
		}
	}

	public <T> T executeGet(SourceQuery q, Class<T> clz) throws ElasticSearchException{
		
		try{
			String url = indexUrl.concat(q.getURI().toString());
			log.debug("Executing GET query {}", url);
			
			T res = elasticSearchRestTemplate.getForObject(url, clz);
			
			log.debug("GET query returned successfully {}", url);
			log.trace("Result: {}", res);
			
			return res;
		}
		catch(RestClientException e){
			throw new ElasticSearchException("Rest exception executing query", e);
		}
	}

}
