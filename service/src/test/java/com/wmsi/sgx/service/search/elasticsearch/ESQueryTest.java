package com.wmsi.sgx.service.search.elasticsearch;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import com.wmsi.sgx.service.search.elasticsearch.ESQuery.EndPoint;

public class ESQueryTest{

	
	@Test
	public void testUriWithType(){
		ESQuery query = new ESQuery();
		query.setEndPoint(EndPoint.SEARCH);
		query.setIndex("testIndex");
		query.setType("mine");
		
		assertEquals(query.getURI().toString(), "/testIndex/mine/_search");
	}
	
	@Test
	public void testUriNoType(){
		ESQuery query = new ESQuery();
		query.setEndPoint(EndPoint.SEARCH);
		query.setIndex("testIndex");
		
		assertEquals(query.getURI().toString(), "/testIndex/_search");
	}

	@Test
	public void testEndpointOnly(){
		ESQuery query = new ESQuery();
		query.setEndPoint(EndPoint.SEARCH);
		
		assertEquals(query.getURI().toString(), "/_search");
	}
	
	@Test
	public void testSearchQueryInstance(){
		ESQuery query = new SearchQuery();

		assertEquals(query.getURI().toString(), "/_search");		
	}
}
