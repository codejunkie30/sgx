package com.wmsi.sgx.service.search.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;

@Service
public class CompanySearchService implements SearchService<SearchCompany>{

	@Autowired
	private ElasticSearchService elasticSearchService;

	private String indexName = "sgx_test";
	private String type = "company";
	
	@Override
	public List<SearchCompany> search(String query) throws SearchServiceException{
		try{
			return elasticSearchService.search(indexName, type, query, SearchCompany.class);			
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Error occurred during search", e);
		}		
	}
	
	@Override
	public SearchCompany getById(String id) throws SearchServiceException{
		try{
			return elasticSearchService.get(indexName, id, SearchCompany.class);
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Error reteriving object by id", e);
		}
	}
}
