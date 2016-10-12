package com.wmsi.sgx.service.search.elasticsearch;

import com.wmsi.sgx.service.search.SearchResult;

/**
 * 
 * This interface is used for declaring methods for elastic search.
 *
 */
public interface ElasticSearchService{

	<T> SearchResult<T> search(String index, String type, String query, Class<T> clz) throws ElasticSearchException;

	<T> T get(String index, String type, String id, Class<T> clz) throws ElasticSearchException;
	
	<T> T getUsingIndex(String index, String type, String id, Class<T> clz) throws ElasticSearchException;
	
	void setIndexName(String indexName);
}