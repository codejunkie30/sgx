package com.wmsi.sgx.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.keydevs.KeyDevsRequest;
import com.wmsi.sgx.service.KeyDevsService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.search.SearchResult;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.query.KeyDevsQueryBuilder;

@Service
public class KeyDevsServiceImpl implements KeyDevsService{

	@Autowired
	private SearchService keyDevsSearch;

	@Override
	@Cacheable("keyDevsSearch")
	public SearchResult<KeyDevs> search(KeyDevsRequest req) throws ServiceException {

		try{
			return keyDevsSearch.search(new KeyDevsQueryBuilder(req), KeyDevs.class);
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}

}
