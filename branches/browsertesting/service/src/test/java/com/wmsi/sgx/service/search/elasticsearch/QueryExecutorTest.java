package com.wmsi.sgx.service.search.elasticsearch;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.CompanyBuilder;
import com.wmsi.sgx.service.search.elasticsearch.impl.ESQueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.impl.SearchQuery;
import com.wmsi.sgx.service.search.elasticsearch.impl.SourceQuery;

@SuppressWarnings("unchecked")
public class QueryExecutorTest{

	private ClassPathResource testJson = new ClassPathResource("elasticsearch/companyResponse.json");
	private ObjectMapper mapper = new ObjectMapper();

	private QueryExecutor executor;	
	private RestTemplate mockTemplate = mock(RestTemplate.class);
	
	@BeforeClass
	public void init() throws RestClientException, JsonProcessingException, IOException{
		ESQueryExecutor ex = new ESQueryExecutor();
		ex.setIndexUrl("unit_test");
		ex.setRestTemplate(mockTemplate);
		executor = ex;
	}
	
	@Test
	public void testSearch() throws ElasticSearchException, RestClientException, JsonProcessingException, IOException {

		SearchQuery query = new SearchQuery();
		query.setQuery("{ \"query\":{ \"match_all\":{} } }");
		
		when(mockTemplate
				.postForObject(any(String.class), any(String.class), any(Class.class)))
				.thenReturn(testJson());
		
		QueryResponse response = executor.executeQuery(query);
		assertNotNull(response);
	}

	@Test(expectedExceptions = {ElasticSearchException.class})
	public void testSearch_RestException() throws ElasticSearchException, IOException{

		SearchQuery query = new SearchQuery();
		query.setQuery("{ \"query\":{ \"match_all\":{} } }");
		
		when(mockTemplate
				.postForObject(any(String.class), any(String.class), any(Class.class)))
				.thenThrow(RestClientException.class);				
	
		executor.executeQuery(query);
	}

	@Test
	public void testGet() throws ElasticSearchException {
		SourceQuery query = new SourceQuery("A7S");
		query.setIndex("test_index");
		query.setType("company");
		
		when(mockTemplate
			.getForObject(any(String.class), any(Class.class)))
			.thenReturn(CompanyBuilder
				.company()
				.withTickerCode("A7S")
				.build());

		Company response = executor.executeGet(query, Company.class);
		
		assertNotNull(response);
		assertEquals(response.getClass(), Company.class);
		assertEquals(response.getTickerCode(), "A7S");
	}

	@Test(expectedExceptions = {ElasticSearchException.class})
	public void testGet_RestException() throws ElasticSearchException {
		
		SourceQuery query = new SourceQuery("A7S");
		query.setIndex("test_index");
		query.setType("company");

		when(mockTemplate
			.getForObject(any(String.class), any(Class.class)))
			.thenThrow(RestClientException.class);
		
		executor.executeGet(query, Company.class);	
		
	}

	private JsonNode testJson() throws IOException{
		return mapper.readTree(testJson.getInputStream());
	}
}
