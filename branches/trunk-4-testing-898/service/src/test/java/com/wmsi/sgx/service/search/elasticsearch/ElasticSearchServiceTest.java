package com.wmsi.sgx.service.search.elasticsearch;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.Collections;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.CompanyBuilder;
import com.wmsi.sgx.service.search.AggregationsBuilder;
import com.wmsi.sgx.service.search.DefaultAggregation;
import com.wmsi.sgx.service.search.DefaultAggregationBuilder;
import com.wmsi.sgx.service.search.SearchResult;
import com.wmsi.sgx.service.search.elasticsearch.impl.ElasticSearchServiceImpl;

@SuppressWarnings("unchecked")
public class ElasticSearchServiceTest{

	private ElasticSearchService service = null;
	
	private QueryExecutor mockExecutor;
	private QueryResponse mockResponse;
	
	@BeforeMethod
	public void init() throws ElasticSearchException{
		
		mockExecutor = mock(QueryExecutor.class);
		mockResponse = mock(QueryResponse.class);
				
		ElasticSearchServiceImpl impl = new ElasticSearchServiceImpl();
		impl.setExecutor(mockExecutor );
		impl.setMapper(new ObjectMapper());

		// Return mock object response
		when(mockExecutor.executeQuery(any(Query.class)))
		.thenReturn(mockResponse);

		service = impl;	
	}
	
	@Test
	public void testSearch() throws ElasticSearchException{	
				
		when(mockResponse.getHits(any(Class.class)))
			.thenReturn(Collections.singletonList(
				CompanyBuilder.company()
				.withTickerCode("C6L")
				.withAvgBrokerReq(9.1D)
				.build()));

		SearchResult<Company> search = service.search("test", null, "query", Company.class);
		
		assertNotNull(search);
		assertNotNull(search.getHits());
		assertEquals(search.getHits().size(), 1);
		assertNull(search.getAggregations());
		assertEquals(search.getHits().get(0).getClass(), Company.class);
		assertEquals(search.getHits().get(0).getTickerCode(), "C6L");
		assertEquals(search.getHits().get(0).getAvgBrokerReq(), 9.1D);
		
		verify(mockExecutor, times(1)).executeQuery(any(Query.class));		
	    verifyNoMoreInteractions(mockExecutor);
	}
	
	@Test
	public void testSearchWithAggregations() throws ElasticSearchException{	
				
		when(mockResponse.getHits(any(Class.class)))
		.thenReturn(Collections.singletonList(
			CompanyBuilder.company()
			.withTickerCode("A7S")
			.withEps(92D)
			.build()));

		when(mockResponse.hasAggregations())
		.thenReturn(true);
		
		when(mockResponse.getAggregations())
		.thenReturn(AggregationsBuilder
				.aggregations()
				.withAddedAggregation(
						DefaultAggregationBuilder
						.defaultAggregation()
						.withName("default")
						.withValue("22.9")
						.build())
				.build());

		SearchResult<Company> search = service.search("test", "type", "query", Company.class);
		
		assertNotNull(search);
		assertNotNull(search.getHits());
		assertEquals(search.getHits().size(), 1);
		assertEquals(search.getHits().get(0).getClass(), Company.class);
		assertEquals(search.getHits().get(0).getTickerCode(), "A7S");
		assertEquals(search.getHits().get(0).getEps(), 92D);
		
		assertNotNull(search.getAggregations());		
		assertEquals(search.getAggregations().getAggregations().size(),1);
		
		DefaultAggregation agg = (DefaultAggregation) search.getAggregations().getAggregations().get(0);
		assertEquals(agg.getName(), "default");
		assertEquals(agg.getValue(), "22.9");
		
		verify(mockExecutor, times(1)).executeQuery(any(Query.class));
	    verifyNoMoreInteractions(mockExecutor);
	}

	@Test
	public void testGetWithAggregations() throws ElasticSearchException{	
				
		when(mockExecutor.executeGet(any(Query.class), any(Class.class)))
		.thenReturn(CompanyBuilder.company()
			.withTickerCode("E25")
			.withClosePrice(55.5D)
			.build());

		Company company = service.get("test", "type", "objectId", Company.class);
		
		assertNotNull(company);
		assertEquals(company.getClass(), Company.class);
		assertEquals(company.getTickerCode(), "E25");
		assertEquals(company.getClosePrice(), 55.5D);
		
		verify(mockExecutor, times(1)).executeGet(any(Query.class), any(Class.class));
	    verifyNoMoreInteractions(mockExecutor);
	}

}

