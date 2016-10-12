package com.wmsi.sgx.service.search.elasticsearch;

/**
 * This abstract class is used for querying based on index, type and company id
 */
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

	public enum SearchType{
		QUERY_THEN_FETCH, DFS_QUERY_THEN_FETCH;
		
		@Override 
		public String toString(){return super.toString().toLowerCase();}
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
	
	private SearchType searchType = SearchType.DFS_QUERY_THEN_FETCH;
	public SearchType getSearchType(){return searchType;}
	public void setSearchType(SearchType t){searchType = t;}
	
	public abstract EndPoint getEndPoint();
	
	/**
	 * Retrieves the end point URI.
	 * 
	 * @return URI
	 */
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
	
		//builder.queryParam("search_type", getSearchType());
		
		return builder.build().toUri();		
	}
	
	/**
	 * Returns JSON object.
	 * 
	 * @return JsonNode
	 * @throws ElasticSearchException
	 */
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
