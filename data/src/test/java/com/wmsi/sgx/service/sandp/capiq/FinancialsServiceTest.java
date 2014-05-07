package com.wmsi.sgx.service.sandp.capiq;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

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

import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestImpl;
import com.wmsi.sgx.service.sandp.capiq.impl.FinancialsResponseParser;
import com.wmsi.sgx.service.sandp.capiq.impl.FinancialsService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class FinancialsServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private FinancialsService financialsService; 
	
	@Test
	public void testLoadFinancials() throws ResponseParserException, CapIQRequestException, ParseException{		
		Financials financials = financialsService.load("A7S", "05/06/2014");
		FinancialsTestUtils.verify(financials);		
	}
	
	@Configuration
	static class FinancialsServiceTestConfig{
		
		@Bean
		public FinancialsService financialsService () throws CapIQRequestException{
			FinancialsService  service = new FinancialsService ();
			service.setRequestExecutor(requestExecutor());
			service.setResponseParser(new FinancialsResponseParser());
			return service;

		}
		
		@Bean
		@SuppressWarnings("unchecked")
		public CapIQRequestExecutor requestExecutor() throws CapIQRequestException {
			
			CapIQRequestExecutor mock = mock(CapIQRequestExecutor.class);
			when(mock.execute( any(CapIQRequestImpl.class), anyMap()) )
				.thenReturn(FinancialsTestUtils.getFinancialsResponse());
			
			return mock;
		}
	}
}
