package com.wmsi.sgx.service.sandp.capiq;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.text.ParseException;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wmsi.sgx.model.financials.Financials;
import com.wmsi.sgx.service.sandp.capiq.impl.FinancialsService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class FinancialsServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private FinancialsService financialsService; 
	
	@Test
	public void testLoadFinancials() throws ResponseParserException, CapIQRequestException, ParseException{		
		Financials financials = financialsService.loadFinancials("A7S", "05/06/2014");
		FinancialsTestUtils.verify(financials);		
	}
	
	@Configuration
	static class CompanyServiceTestConfig{
		
		@Bean
		public FinancialsService financialsService (){
			return new FinancialsService();
		}
		
		@Bean
		@SuppressWarnings("unchecked")
		public CapIQRequestExecutor requestExecutor() throws CapIQRequestException, JsonParseException, JsonMappingException, IOException{
			
			CapIQRequestExecutor mock = mock(CapIQRequestExecutor.class);
			when(mock.execute( any(CapIQRequest.class), anyMap()) )
				.thenReturn(FinancialsTestUtils.getFinancialsResponse());
			
			return mock;
		}

	}


}
