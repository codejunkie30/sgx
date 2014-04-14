package com.wmsi.sgx.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.search.AlphaFactorSearchRequest;
import com.wmsi.sgx.service.AlphaFactorService;
import com.wmsi.sgx.service.AlphaFactorServiceException;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@Service
public class AlphaFactorServiceImpl implements AlphaFactorService{

	@Autowired
	private SearchService<AlphaFactorSearchRequest> alphaFactorSearchService;
	
	@Autowired
	private SearchService<List<AlphaFactor>> alphaFactorService;

	@Override
	@Cacheable(value="alphaFactorSearch")
	public <T> List<T> search(AlphaFactorSearchRequest search, Class<T> clz) throws AlphaFactorServiceException {
		try{
			List<AlphaFactor> alphas = alphaFactorSearchService.search(search, AlphaFactor.class);
			
			return alphaFactorService.search(alphas, clz);
		}
		catch(SearchServiceException e){
			throw new AlphaFactorServiceException("Error loading company data.", e);
		}
	}	
}
