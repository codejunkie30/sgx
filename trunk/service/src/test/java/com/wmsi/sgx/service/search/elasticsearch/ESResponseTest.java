package com.wmsi.sgx.service.search.elasticsearch;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ESResponseTest{

	ObjectMapper mapper = new ObjectMapper();
	
	@Test( expectedExceptions={ElasticSearchException.class})
	public void testNullResponse() throws ElasticSearchException{
		ESResponse response = new ESResponse();
		response.getHits(String.class);		
	}
	
	@Test( expectedExceptions={ElasticSearchException.class},expectedExceptionsMessageRegExp="Response is missing 'hits' field")
	public void testNoHitsField() throws ElasticSearchException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.createObjectNode());
		response.getHits(String.class);		
	}

	@DataProvider
	public Object[][] wrongFields(){
		return new String[][]{
			{"{\"hits\":{\"pits\":[]}}"},
			{"{\"pits\":{\"hits\":[]}}"},
			{"{\"bits\":{\"pits\":[]}}"},
			{"{\"hits\":[]}"}
		};
	}
	@Test( dataProvider="wrongFields", expectedExceptions={ElasticSearchException.class},expectedExceptionsMessageRegExp="Response is missing 'hits' field")
	public void testWrongFields(String json) throws ElasticSearchException, JsonProcessingException, IOException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree(json));
		List<String> hits = response.getHits(String.class);
		assertNotNull(hits);
		assertEquals(hits.size(), 0);		
	}

	@Test
	public void testNoHits() throws ElasticSearchException, JsonProcessingException, IOException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree("{\"hits\":{\"hits\":[]}}"));
		List<String> hits = response.getHits(String.class);
		assertNotNull(hits);
		assertEquals(hits.size(), 0);		
	}

	@Test
	public void testHits() throws ElasticSearchException, JsonProcessingException, IOException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree("{\"hits\":{\"hits\":[{\"_source\":{\"test\":\"test1\"}},{\"_source\":{\"test2\":\"test2\"}}]}}"));
		List<Map> hits = response.getHits(Map.class);
		assertNotNull(hits);
		assertEquals(hits.size(), 2);
		assertEquals(hits.get(0).get("test"), "test1");
		assertEquals(hits.get(1).get("test2"), "test2");
	}

	@Test
	public void testHitsSingleValueType() throws ElasticSearchException, JsonProcessingException, IOException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree("{\"hits\":{\"hits\":{\"_source\":{\"test\":\"test1\"}}}}"));
		List<Map> hits = response.getHits(Map.class);
		assertNotNull(hits);
		assertEquals(hits.size(), 1);
		assertEquals(hits.get(0).get("test"), "test1");		
	}
}
