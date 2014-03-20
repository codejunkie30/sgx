package com.wmsi.sgx.service.search;

import java.util.List;
import java.util.Map;

public interface SearchService{

	<T> List<T> search(String query, Class<T> clz) throws SearchServiceException;
	<T> List<T> search(String query, Map<String, Object> parms, Class<T> clz) throws SearchServiceException;
	<T> T getById(String id, Class<T> clz) throws SearchServiceException;
	<T> List<T> search(Search<T> search, Map<String, Object> parms) throws SearchServiceException;	
}
