package com.wmsi.sgx.service.search.elasticsearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;

public class ESResponse{

	private JsonNode response;
	
	public ESResponse (){}
	
	public ESResponse(JsonNode res){
		response = res;
	}
	
	public JsonNode getResponse(){return response;}
	public void setResponse(JsonNode r){response = r;}

	private ObjectMapper objectMapper  = new ObjectMapper();
	public void setObjectMapper(ObjectMapper m){objectMapper = m;}
	
	public <T> List<T> getHits(Class<T> clz) throws ElasticSearchException{
	
		if(response == null)
			throw new ElasticSearchException("Response is null or empty");
		
		if(response.path("hits").path("hits").isMissingNode())
			throw new ElasticSearchException("Response is missing 'hits' field");
			
		List<T> ret = new ArrayList<T>();
		List<JsonNode> hits = response.get("hits").get("hits").findValues("_source");
		
		for(JsonNode n : hits){
			T hit = objectMapper.convertValue(n, clz);
			ret.add(hit);
		}

		return ret;		
	}

	public Aggregations getAggregations() throws ElasticSearchException{
		
		if(response == null)
			throw new ElasticSearchException("Response is null or empty");
		
		if(response.path("aggregations").isMissingNode())
			throw new ElasticSearchException("Response is missing 'hits' field");
		
		JsonNode aggregations = response.get("aggregations");
		
		List<Aggregation> ret = new ArrayList<Aggregation>();
		Iterator<Entry<String, JsonNode>> i = aggregations.fields();
		
		while(i.hasNext()){
			Entry<String, JsonNode> field = i.next();
			Aggregation agg = objectMapper.convertValue(field.getValue(), Aggregation.class);
			agg.setName(field.getKey());
			ret.add(agg);
		}
		Aggregations aggs = new Aggregations();
		aggs.setAggregations(ret);
		return aggs; 
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("response", response)
			.toString();
	}

}
