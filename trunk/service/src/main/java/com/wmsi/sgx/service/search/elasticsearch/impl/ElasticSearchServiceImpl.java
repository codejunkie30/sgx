package com.wmsi.sgx.service.search.elasticsearch.impl;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.service.search.SearchResult;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;
import com.wmsi.sgx.service.search.elasticsearch.Query;
import com.wmsi.sgx.service.search.elasticsearch.QueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.QueryResponse;


public class ElasticSearchServiceImpl implements ElasticSearchService{

	private QueryExecutor executor;
	private ObjectMapper mapper;
	
	public void setExecutor(QueryExecutor executor) {
		this.executor = executor;
	}
	
	public void setMapper(ObjectMapper m){
		this.mapper = m;
	}

	@Override
	public <T> SearchResult<T> search(String index, String type, String query, Class<T> clz) throws ElasticSearchException{
		Query esQuery = getQuery(index, type, query);
		QueryResponse response = query(esQuery);
		
		SearchResult<T> result = new SearchResult<T>();		
		result.setHits(response.getHits(clz));
		
		if(response.hasAggregations())
			result.setAggregations(response.getAggregations());
		
		return result;
	}		

	@Override
	public <T> T get(String index, String type, String id, Class<T> clz ) throws ElasticSearchException{
		SourceQuery query = new SourceQuery(id);
		query.setIndex(index);
		query.setType(type);
		return executor.executeGet(query, clz);
	}

	private QueryResponse query(Query query) throws ElasticSearchException{
		QueryResponse response = executor.executeQuery(query);
		response.setObjectMapper(mapper);
		return response;		
	}
	
	private Query getQuery(String index, String type, String query){
		SearchQuery esQuery = new SearchQuery();
		esQuery.setIndex(index);
		
		if(StringUtils.isNotEmpty(type))
			esQuery.setType(type);
		
		esQuery.setQuery(query);
		
		return esQuery;		
	}

}
