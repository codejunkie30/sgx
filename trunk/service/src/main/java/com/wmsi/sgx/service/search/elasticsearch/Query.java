package com.wmsi.sgx.service.search.elasticsearch;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

public interface Query{

	URI getURI() throws ElasticSearchException;

	JsonNode toJson() throws ElasticSearchException;

}