package com.wmsi.sgx.service.search.elasticsearch;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import com.wmsi.sgx.service.search.elasticsearch.impl.SourceQuery;

public class SourceQueryTest{

	@Test
	public void testValid() throws ElasticSearchException{
		SourceQuery query = new SourceQuery("223");
		query.setIndex("testIndex");
		query.setType("mine");
		
		assertEquals(query.getURI().toString(), "/testIndex/mine/223/_source");
		
		query = new SourceQuery();
		query.setIndex("testIndex");
		query.setType("mine");
		query.setId("223");
		
		assertEquals(query.getURI().toString(), "/testIndex/mine/223/_source");
	}

	@Test(expectedExceptions={ElasticSearchException.class})
	public void testMissingType() throws ElasticSearchException{
		AbstractQuery query = new SourceQuery("223");
		query.setIndex("testIndex");
		query.getURI();
	}

	@Test(expectedExceptions={ElasticSearchException.class})
	public void testMissingIndex() throws ElasticSearchException{
		AbstractQuery query = new SourceQuery();
		query.setIndex("mine");
		query.setType("testIndex");
		query.getURI();
	}

	@Test(expectedExceptions={ElasticSearchException.class})
	public void testMissingId() throws ElasticSearchException{
		AbstractQuery query = new SourceQuery();
		query.setIndex("testIndex");
		query.setIndex("mine");
		query.getURI();
	}

}
