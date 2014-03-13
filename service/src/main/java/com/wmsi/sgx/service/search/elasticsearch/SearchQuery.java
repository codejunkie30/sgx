package com.wmsi.sgx.service.search.elasticsearch;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchQuery extends ESQuery{

	@Override
	public EndPoint getEndPoint(){return EndPoint.SEARCH;}	
}
