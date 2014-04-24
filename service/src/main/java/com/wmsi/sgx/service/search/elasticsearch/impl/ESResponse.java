package com.wmsi.sgx.service.search.elasticsearch.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;
import com.wmsi.sgx.service.search.Aggregations;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.QueryResponse;

public class ESResponse implements QueryResponse{

	private JsonNode response;
	
	public ESResponse (){}
	
	public ESResponse(JsonNode res){
		response = res;
	}
	
	public JsonNode getResponse(){return response;}
	public void setResponse(JsonNode r){response = r;}

	private ObjectMapper objectMapper  = new ObjectMapper();
	public void setObjectMapper(ObjectMapper m){objectMapper = m;}
	
	@Override
	public <T> List<T> getHits(Class<T> clz) throws ElasticSearchException{
		
		if(response == null)
			throw new ElasticSearchException("Response is null or empty");
		
		if(response.path("hits").path("hits").isMissingNode())
			throw new ElasticSearchException("Response is missing 'hits' field");
			
		List<T> ret = new ArrayList<T>();
		JsonNode hitsNode = response.get("hits").get("hits");
		
		if(!hitsNode.isArray()){
			return Arrays.asList(parseHitNode(hitsNode, clz));
		}
		
		for(JsonNode n : (ArrayNode)hitsNode){
			T hit = parseHitNode(n, clz);
			ret.add(hit);
		}
		
		return ret;		
	}
	
	@Override
	public Aggregations getAggregations() throws ElasticSearchException{
		
		if(response == null)
			throw new ElasticSearchException("Response is null or empty");
		
		if(!hasAggregations())
			throw new ElasticSearchException("Response is missing 'aggregations' field");

		try{
			return objectMapper.treeToValue(response.get("aggregations"), Aggregations.class);
		}
		catch(JsonProcessingException e){
			throw new ElasticSearchException("Error converting json to object",e );
		}
	}

	@Override
	public boolean hasAggregations() throws ElasticSearchException{
		return response != null && !response.path("aggregations").isMissingNode();
	}
	
	private <T> T parseHitNode(JsonNode n, Class<T> clz) throws ElasticSearchException{
		JsonNode srcNode = n.path("_source");
		T hit = objectMapper.convertValue(srcNode, clz);
		
		if(!n.path("fields").isMissingNode()){
			JsonNode fields = flattenFieldValues(n.path("fields"));
			hit = mergeObjects(hit, fields);
		}
		
		return hit;
	}

	private <T> T mergeObjects(T obj, JsonNode fields) throws ElasticSearchException{
		try{
			ObjectReader updater = objectMapper.readerForUpdating(obj);
			return updater.readValue(fields);
		}
		
		catch(IOException e){
			throw new ElasticSearchException("IOException merging objects", e);
		}
	}
	
	/**
	 * Flatten fields node into object values. Elasticsearch fields are always
	 * array, even when they're a leaf node. This method will flatten single
	 * values into Objects for binding to Java objects. 
	 */
	private JsonNode flattenFieldValues(JsonNode fieldNode){
		
		Iterator<Entry<String, JsonNode>> fields = fieldNode.fields();
		ObjectNode node = objectMapper.createObjectNode();
		
		while(fields.hasNext()){
			Entry<String, JsonNode> field = fields.next();
			JsonNode value = null;
			
			if(field.getValue().size() > 1)
				value = field.getValue();
			else
				value = field.getValue().get(0);
			
			node.put(field.getKey(), value);
		}
	
		return node;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("response", response)
			.toString();
	}
}
