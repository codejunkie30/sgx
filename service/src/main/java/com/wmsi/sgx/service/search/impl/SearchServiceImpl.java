package com.wmsi.sgx.service.search.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.wmsi.sgx.service.search.QueryBuilder;
import com.wmsi.sgx.service.search.SearchResult;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;

public class SearchServiceImpl implements SearchService{

	@Autowired
	private ElasticSearchService elasticSearchService;

	public void setElasticSearchService(ElasticSearchService es) {
		elasticSearchService = es;
	}

	private String indexName;
	public void setIndexName(String i){indexName = i;}

	private String type;
	public void setType(String t){type = t;}
		
	@Override
	public <T> T getById(String id, Class<T> clz) throws SearchServiceException {
		try{
			return elasticSearchService.get(indexName, type, id, clz);
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Error reteriving object by id", e);
		}
	}
	
	@Override
	public <T> T getByIdUsingIndexName(String id, String indexname, Class<T> clz) throws SearchServiceException {
		try{  
			return elasticSearchService.getUsingIndex(indexname, type, id, clz);
			
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Error reteriving object by id", e);
		}
	}

	@Override
	public <T> SearchResult<T> search(String query, Class<T> clz) throws SearchServiceException {
		try{
			return elasticSearchService.search(indexName, type, query, clz);
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Exception during search", e);
		}
	}
	
	@Override
	public <T> SearchResult<T>  search(QueryBuilder builder, Class<T> clz) throws SearchServiceException {
		String query = builder.build();
		
		if(query == null)
			return null;
		
		return search(query, clz);		
	}
}
