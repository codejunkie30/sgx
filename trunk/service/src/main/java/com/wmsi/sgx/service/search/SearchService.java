package com.wmsi.sgx.service.search;

public interface SearchService{

	<T> T getById(String id, Class<T> clz) throws SearchServiceException;
	<T> SearchResult<T> search(String q, Class<T> clz) throws SearchServiceException;
	<T> SearchResult<T> search(QueryBuilder builder, Class<T> clz) throws SearchServiceException;
	void setIndexName(String i);
}
