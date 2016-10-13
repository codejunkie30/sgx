package com.wmsi.sgx.service.search.elasticsearch;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * This interface declares the methods for getting end point URI and for
 * returning the JsonNode
 *
 */
public interface Query{

	/**
	 * Retrieves the uri for elastic search.
	 * 
	 * @return URI
	 * @throws ElasticSearchException
	 */
	URI getURI() throws ElasticSearchException;

	/**
	 * Returns JSON object.
	 * 
	 * @return JsonNode
	 * @throws ElasticSearchException
	 */
	JsonNode toJson() throws ElasticSearchException;

}