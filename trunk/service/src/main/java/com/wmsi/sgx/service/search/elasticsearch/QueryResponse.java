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

	/**
	 * Returns the Hits.
	 * 
	 * @param clz
	 *            Class<T>
	 * @return List<T>
	 * @throws ElasticSearchException
	 */
	<T> List<T> getHits(Class<T> clz) throws ElasticSearchException;

	/**
	 * Returns the aggregations.
	 * 
	 * @return Aggregations
	 * @throws ElasticSearchException
	 */
	Aggregations getAggregations() throws ElasticSearchException;

	/**
	 * Checks for the presence of aggregations.
	 * 
	 * @return boolean
	 * @throws ElasticSearchException
	 */
	boolean hasAggregations() throws ElasticSearchException;

	/**
	 * Sets the ObjectMapper
	 * 
	 * @param m
	 *            ObjectMapper
	 */
	void setObjectMapper(ObjectMapper mapper);

}