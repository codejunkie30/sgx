package com.wmsi.sgx.service.search.filter;

import java.util.List;

import com.wmsi.sgx.model.search.Criteria;
import com.wmsi.sgx.service.search.SearchResult;

public interface Filter<T, S>{

	List<T> filter(SearchResult<S> results, List<Criteria> criteria) throws FilterException;
	
}
