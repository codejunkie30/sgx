package com.wmsi.sgx.service.search.elasticsearch;

/**
 * 
 * This interface declares the methods for getting QueryResponse.
 *
 */
public interface QueryExecutor{

	/**
	 * Executes the query and generate the QueryRespone
	 * 
	 * @param q
	 *            Query
	 * @return QueryResponse
	 * @throws ElasticSearchException
	 */
	QueryResponse executeQuery(Query q) throws ElasticSearchException;

	/**
	 * Executes the query and returns the template.
	 * 
	 * @param q
	 *            Query
	 * @param clz
	 *            Class
	 * @return Returns the object out of json document
	 * @throws ElasticSearchException
	 */
	<T> T executeGet(Query q, Class<T> clz) throws ElasticSearchException;

}