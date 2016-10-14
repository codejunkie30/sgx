package com.wmsi.sgx.service.search.elasticsearch;

import com.wmsi.sgx.service.search.SearchResult;

/**
 * 
 * This interface is used for declaring methods for elastic search.
 *
 */
public interface ElasticSearchService{

	/**
	 * Searches based on index, id and type.
	 * 
	 * @param index
	 *            String
	 * @param type
	 *            String
	 * @param query
	 *            String
	 * @param clz
	 *            Class
	 * @return SearchResult
	 * @throws ElasticSearchException
	 */
	<T> SearchResult<T> search(String index, String type, String query, Class<T> clz) throws ElasticSearchException;

	/**
	 * Fetches the query for execution.
	 * 
	 * @param index
	 *            String
	 * @param type
	 *            String
	 * @param id
	 *            id
	 * @param clz
	 *            Class
	 * @return Returns the object out of json document
	 * @throws ElasticSearchException
	 */
	<T> T get(String index, String type, String id, Class<T> clz) throws ElasticSearchException;
	
	/**
	 * Fetches the query for execution based on index.
	 * 
	 * @param index
	 *            String
	 * @param type
	 *            String
	 * @param id
	 *            String
	 * @param clz
	 *            Class
	 * @return Returns the object out of json document 
	 * @throws ElasticSearchException
	 */
	<T> T getUsingIndex(String index, String type, String id, Class<T> clz) throws ElasticSearchException;
	
	/**
	 * Sets the index name
	 * 
	 * @param indexName
	 *            String
	 */
	void setIndexName(String indexName);
}