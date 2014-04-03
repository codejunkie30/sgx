package com.wmsi.sgx.service.search.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ESResponseWrapper{

	private	SearchResponse response;
	
	public ESResponseWrapper(SearchResponse res){
		response = res;
	}
	
	private ObjectMapper objectMapper  = new ObjectMapper();
	public void setObjectMapper(ObjectMapper m){objectMapper = m;}

	public <T> List<T> getHits(Class<T> clz) throws ElasticSearchException{
		
		if(response == null)
			throw new ElasticSearchException("Response is null or empty");
		
		if(response.getHits() == null || response.getHits().getHits() == null)
			throw new ElasticSearchException("Response is missing 'hits' field");
			
		List<T> ret = new ArrayList<T>();
		SearchHit[] hits = response.getHits().getHits();
		
		for(SearchHit h : hits){
			T hit = parseHitNode(h, clz);
			ret.add(hit);
		}
		
		return ret;		
	}
	
	private <T> T parseHitNode(SearchHit h, Class<T> clz) throws ElasticSearchException{

		T hit = null;
		
		try{
			if(!h.isSourceEmpty()){
				hit = objectMapper.readValue(h.getSourceAsString(), clz);
			}
			
			Map<String, SearchHitField> fields = h.getFields();
			
			if(fields != null && fields.size() > 0){
				JsonNode fieldsNode = flattenFieldValues(fields);
				
				if(hit != null){
					hit = mergeObjects(hit, fieldsNode);
				}
				else {
					hit = objectMapper.treeToValue(fieldsNode, clz);
				}
			}
		}
		catch(IOException e){
			throw new ElasticSearchException("Error binding results to object", e);
		}
		
		return hit;
	}
	
	/**
	 * Flatten fields node into object values. Elasticsearch fields are always
	 * array, even when they're a leaf node. This method will flatten single
	 * values into Objects for binding to Java objects. 
	 */
	private JsonNode flattenFieldValues(Map<String, SearchHitField> fields){
		ObjectNode node = objectMapper.createObjectNode();
		
		for(Entry<String, SearchHitField> entry : fields.entrySet()){
			SearchHitField field  = entry.getValue();
			node.putPOJO(entry.getKey(), field.getValue());				
		}
		
		return node;
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

}


