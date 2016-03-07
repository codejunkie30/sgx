package com.wmsi.sgx.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.search.AlphaFactorSearchRequest;
import com.wmsi.sgx.service.AlphaFactorService;
import com.wmsi.sgx.service.AlphaFactorServiceException;
import com.wmsi.sgx.service.search.QueryBuilder;
import com.wmsi.sgx.service.search.SearchResult;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.query.AlphaFactorQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.AlphaFactorSearchQueryBuilder;

@Service
public class AlphaFactorServiceImpl implements AlphaFactorService{

	@Autowired
	private SearchService alphaFactorSearch;
	
	@Autowired
	private SearchService companySearch;

	@Override
	//@Cacheable(value="alphaFactorSearch")
	public <T> List<T> search(AlphaFactorSearchRequest search, Class<T> clz) throws AlphaFactorServiceException {
		try{
			QueryBuilder query = new AlphaFactorSearchQueryBuilder(search);
			SearchResult<AlphaFactor> results = alphaFactorSearch.search(query, AlphaFactor.class);
			
			query = new AlphaFactorQueryBuilder(results.getHits());
			return companySearch.search(query, clz).getHits();
		}
		catch(SearchServiceException e){
			throw new AlphaFactorServiceException("Error loading company data.", e);
		}
	}	
}
