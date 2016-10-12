package com.wmsi.sgx.service.search.elasticsearch.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.Query;
import com.wmsi.sgx.service.search.elasticsearch.QueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.QueryResponse;

/**
 * 
 * This class is used to generating Query Response.
 *
 */
public class ESQueryExecutor implements QueryExecutor{
	
	private static final Logger log = LoggerFactory.getLogger(ESQueryExecutor.class);
	
	private RestTemplate restTemplate;	
	public void setRestTemplate(RestTemplate t){restTemplate = t;	}

	private String indexUrl;	
	public void setIndexUrl(String u){indexUrl = u;}

	/**
	 * Executes the query and generate the QueryRespone
	 * 
	 * @param q
	 *            Query
	 * @return QueryResponse
	 * @throws ElasticSearchException
	 */
	@Override
	public QueryResponse executeQuery(Query q) throws ElasticSearchException{
		
		try{
			log.info("Executing query on {}", indexUrl);
			
			JsonNode query = q.toJson();
			String url = indexUrl.concat(q.getURI().toString());
			
			log.info("URI: {}", q.getURI());
			log.info("Query: {}", query);
			
			JsonNode res = restTemplate.postForObject(url, query, JsonNode.class);
			
			log.info("Query returned successfully");
			log.trace("ES Response: {}", res);
			
			return new ESResponse(res);
		}
		catch(RestClientException e){
			throw new ElasticSearchException("Rest exception executing query", e);
		}
	}

	/**
	 * Executes the query and returns the template.
	 * 
	 * @param q
	 *            Query
	 * @param clz
	 *            Class<T>
	 * @return <T>
	 * @throws ElasticSearchException
	 */
	@Override
	public <T> T executeGet(Query q, Class<T> clz) throws ElasticSearchException{
		
		try{
			String url = indexUrl.concat(q.getURI().toString());
			
			log.debug("Executing GET query {}", url);
			
			T res = restTemplate.getForObject(url, clz);
			
			log.debug("GET query returned successfully {}", url);
			log.trace("Result: {}", res);
			
			return res;
		}
		catch(RestClientException e){
			throw new ElasticSearchException("Rest exception executing query", e);
		}
	}
}
