package com.wmsi.sgx.service.search.elasticsearch;

import java.io.IOException;
import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractQuery implements Query{

	public enum EndPoint {
		SEARCH, SOURCE;
		
		@Override 
		public String toString(){return "_".concat(super.toString().toLowerCase());}
	}
	
	private String index;
	public String getIndex(){return index;}
	public void setIndex(String i){index = i;}
	
	private String type;	
	public String getType(){return type;}
	public void setType(String t){type = t;}

	private String query;
	public String getQuery(){return query;}
	public void setQuery(String q){query = q;}

	private ObjectMapper objectMapper = new ObjectMapper();
	public void setObjectMapper(ObjectMapper m){objectMapper = m;}

	public abstract EndPoint getEndPoint();
	
	@Override
	public URI getURI() throws ElasticSearchException{
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
		
		if(getIndex() != null){
			builder.pathSegment(getIndex());
		
			if(getType() != null)
				builder.pathSegment(getType());
		}
			
		if(getEndPoint() != null)
			builder.pathSegment(getEndPoint().toString());
		
		return builder.build().toUri();		
	}
	
	@Override
	public JsonNode toJson() throws ElasticSearchException{
		JsonNode node = null;
		
		try{
			node = objectMapper.readTree(query);
		}
		catch(IOException e){
			throw new ElasticSearchException("Error parsing string to json", e);
		} 
		
		return node;
	}
}
