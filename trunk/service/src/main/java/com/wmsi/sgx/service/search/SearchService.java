package com.wmsi.sgx.service.search;

import java.util.List;

public interface SearchService{

	<T> List<T> search(String query, Class<T> clz) throws SearchServiceException;	
	<T> T getById(String id, Class<T> clz) throws SearchServiceException;
}
