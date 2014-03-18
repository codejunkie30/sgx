package com.wmsi.sgx.service.search.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.service.search.Search;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;

public class SearchServiceImpl implements SearchService{

	@Autowired
	private ElasticSearchService elasticSearchService;
	public void setElasticSearchService(ElasticSearchService es){elasticSearchService = es;}
	
	private String indexName;
	public void setIndexName(String i){indexName = i;}
	
	private String type;
	public void setType(String t){type = t;}

	@Override
	public <T> List<T> search(String query, Class<T> clz) throws SearchServiceException{
		try{
			return elasticSearchService.search(indexName, type, query, clz);			
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Error occurred during search", e);
		}		
	}

	@Override
	public <T> List<T> search(String query, Map<String, Object> parms, Class<T> clz) throws SearchServiceException{
		try{
			return elasticSearchService.search(indexName, type, query, parms, clz);			
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Error occurred during search", e);
		}		
	}

	@Override
	public <T> List<T> search(Search<T> search, Map<String, Object> parms) throws SearchServiceException{
		try{
			return elasticSearchService.search(search.getIndexName(), search.getType(), search.getQuery(), parms, search.getResultClass());			
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Error occurred during search", e);
		}		
	}

	@Override
	public <T> T getById(String id, Class<T> clz) throws SearchServiceException{
		try{
			return elasticSearchService.get(indexName, type, id, clz);
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Error reteriving object by id", e);
		}		
	}	
}
