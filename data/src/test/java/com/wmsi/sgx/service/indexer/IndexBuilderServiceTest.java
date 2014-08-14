package com.wmsi.sgx.service.indexer;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.service.indexer.impl.IndexBuilderServiceImpl;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQServiceImpl;

@SuppressWarnings("unchecked")
public class IndexBuilderServiceTest{

	private CapIQServiceImpl capIQService;
	private RestTemplate restTemplate; 
	private IndexBuilderService indexBuilderService;
	
	@BeforeMethod
	public void before(){
		
		CapIQRequestExecutor executor = mock(CapIQRequestExecutor.class);
		
		restTemplate = mock(RestTemplate.class);
		executor.setRestTemplate(restTemplate);
		
		capIQService = mock(CapIQServiceImpl.class);
		
		IndexBuilderServiceImpl impl = new IndexBuilderServiceImpl();
		impl.setCapIQService(capIQService);
		
		indexBuilderService = impl;
	}
	
	@Test
	public void testIndex() throws CapIQRequestException, IndexerServiceException, ResponseParserException{
		
		when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class) ))
			.thenThrow(new RestClientException("Test Rest Client Exception"));
		
		CompanyInputRecord rec = indexBuilderService.index("test_index", new CompanyInputRecord());
		assertNotNull(rec);
	}

}
