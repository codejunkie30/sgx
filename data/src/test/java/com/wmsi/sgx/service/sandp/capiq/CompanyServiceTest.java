package com.wmsi.sgx.service.sandp.capiq;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestImpl;
import com.wmsi.sgx.service.sandp.capiq.impl.CompanyResponseParser;
import com.wmsi.sgx.service.sandp.capiq.impl.CompanyService;
import com.wmsi.sgx.service.sandp.capiq.impl.HistoricalService;
import com.wmsi.sgx.util.test.TestUtils;

@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class CompanyServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private CompanyService companyService;
	
	//@Test
	public void testLoadCompany() throws ResponseParserException, CapIQRequestException, ParseException{

		Company company = companyService.load("A7S", "05/06/2014");
		CompanyTestUtils.verify(company);
		
		// Test additional derived fields
		assertEquals(company.getAvgVolumeM3(), 9.6798D);
		assertEquals(company.getPriceHistory().size(), 253);
		assertEquals(company.getPreviousClosePrice(), 0.48D);
	}
	
	@Configuration
	static class CompanyServiceTestConfig{
		
		private ObjectMapper mapper = TestUtils.getObjectMapper();
		
		@Bean
		public CompanyService companyService() throws CapIQRequestException{
			CompanyService service = new CompanyService();
			service.setRequestExecutor(requestExecutor());
			service.setResponseParser(new CompanyResponseParser());
			return service;
		}
		
		@Bean
		@SuppressWarnings("unchecked")
		public HistoricalService historicalService() throws JsonParseException, JsonMappingException, IOException, CapIQRequestException{
			Resource json = new ClassPathResource("data/capiq/priceHistoryY1.json");
			CapIQResponse response = mapper.readValue(json.getInputStream(), CapIQResponse.class);

			CapIQRequestExecutor mock = mock(CapIQRequestExecutor.class);
			
			when(mock.execute( any(CapIQRequestImpl.class), anyMap()) )
				.thenReturn(response);
			when(mock.loadFiles(anyString(), anyString()))
			.thenReturn(new ArrayList<String>(Arrays.asList("pricehistory_A7S_05-06-2014.json")));
			when(mock.readFile(anyString()))
			.thenReturn(response);

			 HistoricalService serv = new HistoricalService();
			 serv.setRequestExecutor(mock);
			 return serv;
		}
		
		@Bean
		@SuppressWarnings("unchecked")
		public CapIQRequestExecutor requestExecutor() throws CapIQRequestException{
			
			CapIQResponse response = CompanyTestUtils.getCompanyResponse();

			CapIQRequestExecutor mock = mock(CapIQRequestExecutor.class);
			when(mock.execute( any(CapIQRequestImpl.class), anyMap()) )
				.thenReturn(response);
			
			
			return mock;
		}

	}

}
