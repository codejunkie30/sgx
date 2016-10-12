package com.wmsi.sgx.service.search.elasticsearch;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.service.search.Aggregations;

/**
 * 
 * This interface declares the methods for getting Aggregations, Hits and
 * ObjectMapper.
 *
 */
public interface QueryResponse{

	<T> List<T> getHits(Class<T> clz) throws ElasticSearchException;

	Aggregations getAggregations() throws ElasticSearchException;

	boolean hasAggregations() throws ElasticSearchException;

	void setObjectMapper(ObjectMapper mapper);

}