package com.wmsi.sgx.service.sandp.capiq;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestImpl;
import com.wmsi.sgx.service.sandp.capiq.impl.HoldersResponseParser;
import com.wmsi.sgx.service.sandp.capiq.impl.HoldersService;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class HoldersServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private HoldersService holdersService;
	
	//@Test
	public void testHolders() throws CapIQRequestException, ResponseParserException{

		Holders holders = holdersService.load("A7S");
		HoldersTestUtils.verify(holders);
		
	}
	
	@Configuration
	static class HoldersServiceTestConfig{

		@Bean
		public HoldersService keyDevsService() throws CapIQRequestException {
			HoldersService service = new HoldersService();
			service.setRequestExecutor(requestExecutor());
			service.setResponseParser(new HoldersResponseParser());
			return service;
		}

		@Bean
		@SuppressWarnings("unchecked")
		public CapIQRequestExecutor requestExecutor() throws CapIQRequestException {

			CapIQResponse response = HoldersTestUtils.getResponse();

			CapIQRequestExecutor mock = mock(CapIQRequestExecutor.class);
			when(mock.execute(any(CapIQRequestImpl.class), anyMap())).thenReturn(response);

			return mock;
		}
	}
}
