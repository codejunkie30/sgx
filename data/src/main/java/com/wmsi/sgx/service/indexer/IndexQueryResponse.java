package com.wmsi.sgx.service.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;

public class IndexQueryResponse {
	
	private static final Logger log = LoggerFactory.getLogger(IndexQueryResponse.class);
	
	JsonNode response;

	public IndexQueryResponse (){}
	
	public IndexQueryResponse(JsonNode res){
		response = res;
	}
	
	public JsonNode getResponse(){return response;}
	public void setResponse(JsonNode r){response = r;}

	private ObjectMapper objectMapper  = new ObjectMapper();
	public void setObjectMapper(ObjectMapper m){objectMapper = m;}
	
	/**
	 * Generalized utility method to get response from ES based on a class type
	 * 
	 * @param  class type 
	 * @return generalized type List 
	 * 
	 * @throws IndexerServiceException
	 */
	public <T> List<T> getHits(Class<T> clz) throws IndexerServiceException {
		
		if(response == null) throw new IndexerServiceException("Response is null or empty");
		
		if(response.path("hits").path("hits").isMissingNode()) throw new IndexerServiceException("Response is missing 'hits' field");
			
		List<T> ret = new ArrayList<T>();
		JsonNode hitsNode = response.get("hits").get("hits");
		
		if(!hitsNode.isArray()) return Arrays.asList(parseHitNode(hitsNode, clz));
		
		
		for(JsonNode n : (ArrayNode)hitsNode){
			T hit = parseHitNode(n, clz);
			ret.add(hit);
		}
		
		return ret;		
	}
	
	private <T> T parseHitNode(JsonNode n, Class<T> clz) throws IndexerServiceException {
		
		JsonNode srcNode = n.path("_source");
		T hit = objectMapper.convertValue(srcNode, clz);
		
		if(!n.path("fields").isMissingNode()){
			JsonNode fields = flattenFieldValues(n.path("fields"));
			hit = mergeObjects(hit, fields);
		}
		
		return hit;
	}

	private <T> T mergeObjects(T obj, JsonNode fields) throws IndexerServiceException {
		try{
			ObjectReader updater = objectMapper.readerForUpdating(obj);
			return updater.readValue(fields);
		}
		
		catch(IOException e){
			throw new IndexerServiceException("IOException merging objects", e);
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

