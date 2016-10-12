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

	URI getURI() throws ElasticSearchException;

	JsonNode toJson() throws ElasticSearchException;

}