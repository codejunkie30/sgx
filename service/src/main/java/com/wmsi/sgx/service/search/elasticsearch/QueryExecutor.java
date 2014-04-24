package com.wmsi.sgx.service.search.elasticsearch;

public interface QueryExecutor{

	QueryResponse executeQuery(Query q) throws ElasticSearchException;

	<T> T executeGet(Query q, Class<T> clz) throws ElasticSearchException;

}