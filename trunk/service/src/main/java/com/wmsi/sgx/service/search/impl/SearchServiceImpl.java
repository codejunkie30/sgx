package com.wmsi.sgx.service.search.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.wmsi.sgx.service.search.QueryBuilder;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;

public class SearchServiceImpl<S> implements SearchService<S>{

	@Autowired
	private ElasticSearchService elasticSearchService;

	public void setElasticSearchService(ElasticSearchService es) {
		elasticSearchService = es;
	}

	private String indexName;
	public void setIndexName(String i){indexName = i;}

	private String type;
	public void setType(String t){type = t;}

	private QueryBuilder<S> queryBuilder;
	public void setQueryBuilder(QueryBuilder<S> b){queryBuilder = b;}
		
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
	public <T> List<T> search(S search, Class<T> clz) throws SearchServiceException {

		String query = queryBuilder.build(search);

		try{
			return elasticSearchService.search(indexName, type, query, clz);
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Could not load related companies", e);
		}
	}

}
