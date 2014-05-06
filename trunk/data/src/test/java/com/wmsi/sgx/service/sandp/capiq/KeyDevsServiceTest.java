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

import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.KeyDevsService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class KeyDevsServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private KeyDevsService keyDevsService;

	@Test
	public void testLoadKeyDevs() throws ResponseParserException, CapIQRequestException, ParseException {

		KeyDevs devs = keyDevsService.loadKeyDevelopments("A7S", "05/06/2014");
		KeyDevsTestUtils.verify(devs);
		
	}

	@Configuration
	static class KeyDevsServiceTestConfig{

		@Bean
		public KeyDevsService keyDevsService() {
			return new KeyDevsService();
		}

		@Bean
		@SuppressWarnings("unchecked")
		public CapIQRequestExecutor requestExecutor() throws CapIQRequestException {

			CapIQResponse response = KeyDevsTestUtils.getResponse();

			CapIQRequestExecutor mock = mock(CapIQRequestExecutor.class);
			when(mock.execute(any(CapIQRequest.class), anyMap())).thenReturn(response);

			when(mock.execute(any(String.class))).thenReturn(response);

			return mock;
		}

	}

}
