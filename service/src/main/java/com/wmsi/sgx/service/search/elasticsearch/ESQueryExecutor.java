package com.wmsi.sgx.service.search.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class ESQueryExecutor{
	
	private static final Logger log = LoggerFactory.getLogger(ESQueryExecutor.class);
	
	@Autowired
	@Qualifier("esRestTemplate")
	RestTemplate elasticSearchRestTemplate;
	
	private String url = "http://localhost:9200/sgx_20140304/company/_search";
	
	public ESResponse executeQuery(ESQuery q) throws ElasticSearchException{
		
		try{
			log.debug("Executing query on {}", url);
			
			JsonNode query = q.toJson();
			
			log.trace("Query: {}", query);
			
			JsonNode res = elasticSearchRestTemplate.postForObject(url, query, JsonNode.class);
			
			log.debug("Query returned successfully");
			log.trace("ES Response: {}", res);
			
			return new ESResponse(res);
		}
		catch(RestClientException e){
			throw new ElasticSearchException("Rest exception executing query", e);
		}
	}
}
