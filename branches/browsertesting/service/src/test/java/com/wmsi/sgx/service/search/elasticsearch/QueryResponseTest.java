package com.wmsi.sgx.service.search.elasticsearch;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.service.search.Aggregations;
import com.wmsi.sgx.service.search.BucketAggregation;
import com.wmsi.sgx.service.search.DefaultAggregation;
import com.wmsi.sgx.service.search.StatAggregation;
import com.wmsi.sgx.service.search.elasticsearch.impl.ESResponse;

@SuppressWarnings("rawtypes")
public class QueryResponseTest{

	ObjectMapper mapper = new ObjectMapper();
	
	@Test( expectedExceptions={ElasticSearchException.class})
	public void testNullResponse() throws ElasticSearchException{
		ESResponse response = new ESResponse();
		response.getHits(String.class);		
	}

	@Test( expectedExceptions={ElasticSearchException.class})
	public void testNullResponse_Aggregations() throws ElasticSearchException{
		ESResponse response = new ESResponse();
		response.getAggregations();		
	}

	@Test( expectedExceptions={ElasticSearchException.class},
			expectedExceptionsMessageRegExp="Response is missing 'hits' field")
	public void testEmptyResponse() throws ElasticSearchException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.createObjectNode());
		response.getHits(String.class);		
	}

	@Test( expectedExceptions={ElasticSearchException.class},
		   expectedExceptionsMessageRegExp="Response is missing 'aggregations' field")
	public void testEmptyResponse_Aggregations() throws ElasticSearchException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.createObjectNode());
		response.getAggregations();		
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

	@Test(dataProvider="wrongFields", 
		expectedExceptions={ElasticSearchException.class},
		expectedExceptionsMessageRegExp="Response is missing 'hits' field")
	public void testWrongFields_Hits(String json) throws ElasticSearchException, JsonProcessingException, IOException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree(json));
		response.getHits(String.class);
	}

	@Test(dataProvider="wrongFields", 
		expectedExceptions={ElasticSearchException.class},
		expectedExceptionsMessageRegExp="Response is missing 'aggregations' field")
	public void testWrongFields_Aggregations(String json) throws ElasticSearchException, JsonProcessingException, IOException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree(json));
		response.getAggregations();				
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

	/**
	 * Test elasticsearch responses which is the 'field' setting to include/exlude fields. ESResponse will 
	 * attempt to merge these into top level objects. This test proves that it's working correctly.  
	 */
	@Test
	public void testHitsWithFields() throws ElasticSearchException, JsonProcessingException, IOException{
		ESResponse response = new ESResponse();
		response.setResponse(mapper.readTree(
				"{\"hits\":"
				+ "{\"hits\":["						
				+ "{\"_source\":{\"test\":\"test1\"},\"fields\":{\"percentChange\":[-0.011999999997], \"beta5Yr\":[1.826588992]}},"
				+ "{\"_source\":{\"test2\":\"test2\"}},"
				+ "{\"_source\":{\"test3\":\"test3\"},\"fields\":{\"prices\":[1.99997, 2.123, 4.2]}},"
				+ "{\"_source\":{\"test4\":\"test4\"},\"fields\":{\"prices\":[2.9, 11.3 ], \"vol\":[5.8, 0.2]}}"
				+ "]}}"));
		
		List<Map> hits = response.getHits(Map.class);

		assertNotNull(hits);
		assertEquals(hits.size(), 4);

		// Multiple fields with singel values
		assertEquals(hits.get(0).get("test"), "test1");
		assertEquals(hits.get(0).get("percentChange"), -0.011999999997);
		assertEquals(hits.get(0).get("beta5Yr"), 1.826588992);
		
		// No fields object
		assertEquals(hits.get(1).get("test2"), "test2");
		assertNull(hits.get(1).get("percentChange"));
		
		// Single field with array value
		assertEquals(hits.get(2).get("test3"), "test3");
		List prices = (List) hits.get(2).get("prices");		
		assertEquals(prices.size(), 3);
		assertEquals(prices.get(0), 1.99997);
		assertEquals(prices.get(1), 2.123);
		assertEquals(prices.get(2), 4.2);
		
		// Multi field with array values
		assertEquals(hits.get(3).get("test4"), "test4");
		prices = (List) hits.get(3).get("prices");		
		List vols = (List) hits.get(3).get("vol");
		assertEquals(prices.size(), 2);
		assertEquals(prices.get(0), 2.9);
		assertEquals(prices.get(1), 11.3);
		assertEquals(vols.get(0), 5.8);
		assertEquals(vols.get(1), 0.2);
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
	public void testHasAggregation() throws JsonProcessingException, IOException, ElasticSearchException{
		
		ESResponse response = new ESResponse();
		assertFalse(response.hasAggregations());
		
		response.setResponse(mapper.readTree("{\"hits\":{\"hits\":{\"_source\":{\"test\":\"test1\"}}}}"));
		assertFalse(response.hasAggregations());
		
		response.setResponse(mapper.readTree(testSingleBucketAggregation));
		assertTrue(response.hasAggregations());
	}
	
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
	