package com.wmsi.sgx.service.search.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.wmsi.sgx.service.search.QueryBuilder;


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
			
			log.error("Query: {}", query);
			
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
	/*
	public ESResponseWrapper nativeSearch(SearchSourceBuilder builder) throws ElasticSearchException{
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "jml.es.test").build();
		
		Client client = new TransportClient(settings)
        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		
		SearchResponse response = new SearchRequestBuilder(client)
			.internalBuilder(builder)
			.setIndices("sgx_test")
			.execute()
			.actionGet();
        
        return new ESResponseWrapper(response);
		
	}*/

}
