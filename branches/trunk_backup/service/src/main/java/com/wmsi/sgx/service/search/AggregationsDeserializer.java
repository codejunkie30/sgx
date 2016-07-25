package com.wmsi.sgx.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AggregationsDeserializer extends JsonDeserializer<Object>{

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public void setMapper(ObjectMapper m){mapper = m;}

	@Override
	public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectCodec oc = jp.getCodec();
		JsonNode node = oc.readTree(jp);

		Iterator<Entry<String, JsonNode>> i = node.fields();
		List<Aggregation> ret = new ArrayList<Aggregation>();

		while(i.hasNext()){
			Entry<String, JsonNode> field = i.next();
			JsonNode fieldNode = field.getValue();
			
			Aggregation agg = aggregationByType(fieldNode);			
			agg.setName(field.getKey());
			
			ret.add(agg);
		}

		Aggregations aggs = new Aggregations();
		aggs.setAggregations(ret);

		return aggs;
	}
	
	private Aggregation aggregationByType(JsonNode fieldNode) throws JsonProcessingException{
		Aggregation agg = new Aggregation();

    	JsonNode subAgg = getSubAggregation(fieldNode);
    	
		if(subAgg != null){
			// Kind of a hack to return sub aggregations as top level
			return aggregationByType(subAgg);    			
		}

        if(fieldNode.has("buckets")){
        	agg = mapper.treeToValue(fieldNode, BucketAggregation.class);
        }
        else if(fieldNode.has("min")){
            agg = mapper.treeToValue(fieldNode, StatAggregation.class);
        }
        else{
        	DefaultAggregation def = new DefaultAggregation();
        	def.setValue(mapper.writeValueAsString(fieldNode));
        	agg = def;
        }

        return agg; 
	}
	
	private JsonNode getSubAggregation(JsonNode n){
		Iterator<Entry<String, JsonNode>> fields = n.fields();
		
		while(fields.hasNext()){
			JsonNode field = fields.next().getValue();
			
			if(field.isObject())
				return field; 
		}
			
		return null;
	}

}
