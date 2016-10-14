package com.wmsi.sgx.service.search.elasticsearch.impl;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.service.search.SearchResult;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;
import com.wmsi.sgx.service.search.elasticsearch.Query;
import com.wmsi.sgx.service.search.elasticsearch.QueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.QueryResponse;

/**
 * 
 * This class is used for searching the companies based on index and id.
 *
 */
public class ElasticSearchServiceImpl implements ElasticSearchService{

	private QueryExecutor executor;
	private ObjectMapper mapper;
	//default value
	private String indexName="sgd_premium";
	
	/**
	 * Sets the QueryExecutor for executing the queries.
	 * 
	 * @param executor
	 *            QueryExecutor
	 */
	public void setExecutor(QueryExecutor executor) {
		this.executor = executor;
	}
	
	/**
	 * Sets the ObjectMapper
	 * 
	 * @param m
	 *            ObjectMapper
	 */
	public void setMapper(ObjectMapper m){
		this.mapper = m;
	}
	
	/**
	 * Sets the index name
	 * 
	 * @param indexName
	 *            String
	 */
	public void setIndexName(String indexName){
		this.indexName=indexName;
	}

	/**
	 * Searches based on index, id and type.
	 * 
	 * @param index
	 *            String
	 * @param type
	 *            String
	 * @param query
	 *            String
	 * @param clz
	 *            Class
	 * @return Returns the object out of json document
	 * @throws ElasticSearchException
	 */
	@Override
	public <T> SearchResult<T> search(String index, String type, String query, Class<T> clz) throws ElasticSearchException{
		Query esQuery = getQuery(this.indexName, type, query);
		QueryResponse response = query(esQuery);
		
		SearchResult<T> result = new SearchResult<T>();		
		result.setHits(response.getHits(clz));
		
		if(response.hasAggregations())
			result.setAggregations(response.getAggregations());
		
		return result;
	}		

	/**
	 * Fetches the query for execution.
	 * 
	 * @param index
	 *            String
	 * @param type
	 *            String
	 * @param id
	 *            String
	 * @param clz
	 *            Class
	 * @return Returns the object out of json document
	 * @throws ElasticSearchException
	 */
	@Override
	public <T> T get(String index, String type, String id, Class<T> clz ) throws ElasticSearchException{
		SourceQuery query = new SourceQuery(id);
		query.setIndex(this.indexName);
		query.setType(type);
		return executor.executeGet(query, clz);
	}
	
	/**
	 * Fetches the query for execution based on index.
	 * 
	 * @param index
	 *            String
	 * @param type
	 *            String
	 * @param id
	 *            String
	 * @param clz
	 *            Class
	 * @return Returns the object out of json document
	 * @throws ElasticSearchException
	 */
	@Override
	public <T> T getUsingIndex(String index, String type, String id, Class<T> clz ) throws ElasticSearchException{
		SourceQuery query = new SourceQuery(id);
		query.setIndex(index);
		query.setType(type);
		return executor.executeGet(query, clz);
	}

	/**
	 * Fetches response after execution of query.
	 * 
	 * @param query
	 *            Query
	 * @return QueryResponse
	 * @throws ElasticSearchException
	 */
	private QueryResponse query(Query query) throws ElasticSearchException{
		QueryResponse response = executor.executeQuery(query);
		response.setObjectMapper(mapper);
		return response;		
	}
	
	/**
	 * Fetches query for execution.
	 * 
	 * @param idex
	 *            Query
	 * @param type
	 *            Query
	 * @param query
	 *            Query
	 * @return Query
	 */
	private Query getQuery(String index, String type, String query){
		SearchQuery esQuery = new SearchQuery();
		esQuery.setIndex(this.indexName);
		
		if(StringUtils.isNotEmpty(type))
			esQuery.setType(type);
		
		esQuery.setQuery(query);
		
		return esQuery;		
	}

}
