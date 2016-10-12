package com.wmsi.sgx.service.search.elasticsearch;

/**
 * 
 * This interface declares the methods for getting QueryResponse.
 *
 */
public interface QueryExecutor{

	QueryResponse executeQuery(Query q) throws ElasticSearchException;

	<T> T executeGet(Query q, Class<T> clz) throws ElasticSearchException;

}