package com.wmsi.sgx.model.search;

import java.io.IOException;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RangeQueryTest{

	public String rangeJson = "{\"type\": \"range\", \"field\": \"marketCap\",\"range\": {\"gte\": 2,\"lte\": 500}}";
	
	@Test
	public void testJsonBind() throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper m = new ObjectMapper();
		//AbstractQuery query = m.readValue(rangeJson, AbstractQuery.class);
		//System.out.println(query);
	}
	
	@Test
	public void testJsonSerialize() throws JsonParseException, JsonMappingException, IOException{
		RangeQuery rq = new RangeQuery();
		rq.setField("marketCap");
		rq.setRange(new Range(5.0, 100.0));
		ObjectMapper m = new ObjectMapper();
		System.out.println(m.writeValueAsString(rq));
	}
}
