package com.wmsi.sgx.service.search.elasticsearch;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

public class SearchQueryTest{

	
	@Test
	public void testUriWithType() throws ElasticSearchException{
		ESQuery query = new SearchQuery();
		query.setIndex("testIndex");
		query.setType("mine");
		
		assertEquals(query.getURI().toString(), "/testIndex/mine/_search");
	}
	
	@Test
	public void testUriNoType() throws ElasticSearchException{
		ESQuery query = new SearchQuery();
		query.setIndex("testIndex");
		
		assertEquals(query.getURI().toString(), "/testIndex/_search");
	}

	@Test
	public void testEndpointOnly() throws ElasticSearchException{
		ESQuery query = new SearchQuery();
		assertEquals(query.getURI().toString(), "/_search");
	}
	
	@Test
	public void testQueryParsing() throws ElasticSearchException{
		ESQuery query = new SearchQuery();
		assertEquals(query.getURI().toString(), "/_search");
	}

}
