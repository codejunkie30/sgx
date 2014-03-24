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
	
	private String testSingleBucketAggregation = "{\"aggregations\": {\"marketCap\":{\"buckets\":[{\"key\": 0,	\"doc_count\": 1},{\"key\": 15,	\"doc_count\": 5}]}}}";
	private String testMultipleBucketAggregations = "{\"aggregations\": {\"marketCap\":{\"buckets\":[{\"key\": 0,	\"doc_count\": 1},{\"key\": 15,	\"doc_count\": 5}]},\"totalRev\":{\"buckets\":[{\"key\": 0,\"doc_count\": 1},{\"key\": 15,\"doc_count\": 5}]}}} }";
	
	@Test
	public void testBucketAggregation() throws JsonProcessingException, IOException, ElasticSearchException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree(testSingleBucketAggregation));
		Aggregations aggregations = response.getAggregations();
		assertNotNull(aggregations);
		assertEquals(aggregations.getAggregations().size(), 1);
		assertEquals(aggregations.getAggregations().get(0).getName(), "marketCap");
	}

	@Test
	public void testMultipleBucketAggregations() throws JsonProcessingException, IOException, ElasticSearchException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree(testMultipleBucketAggregations));
		Aggregations aggregations = response.getAggregations();
		assertNotNull(aggregations);
		assertEquals(aggregations.getAggregations().size(), 2);
		assertEquals(aggregations.getAggregations().get(0).getName(), "marketCap");
		assertEquals(aggregations.getAggregations().get(1).getName(), "totalRev");
		assertEquals(aggregations.getAggregations().get(0).getClass(), BucketAggregation.class);
		
		BucketAggregation ba = (BucketAggregation)aggregations.getAggregations().get(0);
		BucketAggregation ba2 = (BucketAggregation)aggregations.getAggregations().get(1);
		assertEquals(ba.getBuckets().size(), 2);
		assertEquals(ba.getBuckets().get(0).getKey(), 0);
		assertEquals(ba.getBuckets().get(0).getCount().longValue(), 1);
		
		assertEquals(ba2.getBuckets().size(), 2);
		assertEquals(ba.getBuckets().get(1).getKey(), 15);
		assertEquals(ba.getBuckets().get(1).getCount().longValue(), 5);
	}
	
	private String testSingleStatsAggregation = "{\"aggregations\":{\"marketCap\":{\"count\":68,\"min\":5.244449,\"max\": 2523.873173,\"avg\":1160.7159981323528,\"sum\":78928.68787299999}}}";
		
	@Test
	public void testStatsAggregations() throws JsonProcessingException, IOException, ElasticSearchException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree(testSingleStatsAggregation));
		Aggregations aggregations = response.getAggregations();
		assertNotNull(aggregations);
		assertEquals(aggregations.getAggregations().size(), 1);
		assertEquals(aggregations.getAggregations().get(0).getName(), "marketCap");
		assertEquals(aggregations.getAggregations().get(0).getClass(), StatAggregation.class);
		
		StatAggregation agg = (StatAggregation) aggregations.getAggregations().get(0);
		assertEquals(agg.getCount().intValue(), 68);
		assertEquals(agg.getMin(), 5.244449);
		assertEquals(agg.getMax(), 2523.873173);
		assertEquals(agg.getAvg(), 1160.7159981323528);
		assertEquals(agg.getSum(), 78928.68787299999);
	}

	private String testDefaultAggregation = "{\"aggregations\":{\"marketCap\":{\"UnknownType\": 689}}}";
	
	@Test
	public void testDefaultTypeAggregations() throws JsonProcessingException, IOException, ElasticSearchException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree(testDefaultAggregation));
		Aggregations aggregations = response.getAggregations();
		assertNotNull(aggregations);
		assertEquals(aggregations.getAggregations().get(0).getClass(), DefaultAggregation.class);
		DefaultAggregation agg = (DefaultAggregation) aggregations.getAggregations().get(0);
		assertEquals(agg.getValue(), "{\"UnknownType\":689}");
	}
	
	private String testMixedAggregation = "{\"aggregations\":{\"marketCap\":{\"count\": 68,\"min\": 5.244449,\"max\": 2523.873173,\"avg\": 1160.7159981323528,	\"sum\": 78928.68787299999},\"marketCap_bucket\":{\"buckets\":[{\"key\": 0,\"doc_count\": 1},{\"key\": 15,\"doc_count\": 5}]}}}";
	
	@Test
	public void testMixedAggregations() throws JsonProcessingException, IOException, ElasticSearchException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree(testMixedAggregation));
		Aggregations aggregations = response.getAggregations();
		assertNotNull(aggregations);
		assertEquals(aggregations.getAggregations().get(0).getClass(), StatAggregation.class);
		assertEquals(aggregations.getAggregations().get(1).getClass(), BucketAggregation.class);
	}
}
	