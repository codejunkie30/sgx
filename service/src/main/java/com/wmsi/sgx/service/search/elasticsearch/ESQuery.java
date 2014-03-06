package com.wmsi.sgx.service.search.elasticsearch;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ESQuery{

	private String queryTemplate;
	
	public String getQueryTemplate(){return queryTemplate;}
	public void setQueryTemplate(String q){queryTemplate = q;}

	private ObjectMapper objectMapper = new ObjectMapper();
	public void setObjectMapper(ObjectMapper m){objectMapper = m;}

	public JsonNode toJson() throws ElasticSearchException{
		JsonNode node = null;
		
		try{
			node = objectMapper.readTree(queryTemplate);
		}
		catch(IOException e){
			throw new ElasticSearchException("Error parsing string to json", e);
		} 
		
		return node;
	}
}
