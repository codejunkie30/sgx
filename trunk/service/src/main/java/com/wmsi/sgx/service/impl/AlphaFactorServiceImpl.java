package com.wmsi.sgx.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.alpha.AlphaFactorSearchRequest;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.AlphaFactorService;
import com.wmsi.sgx.service.AlphaFactorServiceException;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.query.AlphaFactorQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.AlphaFactorSearchQueryBuilder;

@Service
public class AlphaFactorServiceImpl implements AlphaFactorService{

	@Autowired
	private SearchService alphaFactorSearchService;
	
	@Autowired
	private SearchService companySearchService;

	@Override
	public <T> List<T> search(AlphaFactorSearchRequest search, Class<T> clz) throws AlphaFactorServiceException {
		try{
			String alphaSearch = new AlphaFactorSearchQueryBuilder().build(search);
			List<AlphaFactor> alphas = alphaFactorSearchService.search(alphaSearch, AlphaFactor.class);
			
			String alphaFactors = new AlphaFactorQueryBuilder().build(alphas);
			return companySearchService.search(alphaFactors, clz);
		}
		catch(SearchServiceException e){
			throw new AlphaFactorServiceException("Error loading company data.", e);
		}
	}	
}
