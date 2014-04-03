package com.wmsi.sgx.service.search;

import java.util.List;

public interface SearchService<S>{

	<T> T getById(String id, Class<T> clz) throws SearchServiceException;
	<T> List<T> search(S q, Class<T> clz) throws SearchServiceException;	
}
