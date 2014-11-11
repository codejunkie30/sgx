package com.wmsi.sgx.service;

import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.keydevs.KeyDevsRequest;
import com.wmsi.sgx.service.search.SearchResult;

public interface KeyDevsService{
	
	SearchResult<KeyDevs> search(KeyDevsRequest req) throws ServiceException;

}