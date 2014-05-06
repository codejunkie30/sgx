package com.wmsi.sgx.service.sandp.capiq;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.HoldersService;
import com.wmsi.sgx.util.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class HoldersServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private HoldersService holdersService;
	
	@Test
	public void testHolders() throws CapIQRequestException{
		Holders holders = holdersService.loadHolders("A7S");
		assertNotNull(holders);
		assertEquals(holders.getHolders().size(), 10);
		
		assertEquals(holders.getHolders().get(0).getName(), "Sun, Liping");
		assertEquals(holders.getHolders().get(0).getPercent(), 61.67935);
		assertEquals(holders.getHolders().get(0).getShares(), new Long(329152241));
		
		assertEquals(holders.getHolders().get(4).getName(), "Vantagepoint Investment Advisers, LLC");
		assertEquals(holders.getHolders().get(4).getPercent(), 1.11644);
		assertEquals(holders.getHolders().get(4).getShares(), new Long(5957900));

		
		assertEquals(holders.getHolders().get(9).getName(), "CIMB-Principal Asset Management Bhd");
		assertEquals(holders.getHolders().get(9).getPercent(), 0.47690);
		assertEquals(holders.getHolders().get(9).getShares(), new Long(2545000));

	}
	
	@Configuration
	static class HoldersServiceTestConfig{

		private ObjectMapper mapper = TestUtils.getObjectMapper();

		@Bean
		public HoldersService keyDevsService() {
			return new HoldersService();
		}

		@Bean
		@SuppressWarnings("unchecked")
		public CapIQRequestExecutor requestExecutor() throws CapIQRequestException, JsonParseException,
				JsonMappingException, IOException {

			Resource json = new ClassPathResource("data/capiq/holdersResponse.json");
			CapIQResponse response = mapper.readValue(json.getInputStream(), CapIQResponse.class);

			CapIQRequestExecutor mock = mock(CapIQRequestExecutor.class);
			when(mock.execute(any(CapIQRequest.class), anyMap())).thenReturn(response);

			when(mock.execute(any(String.class))).thenReturn(response);

			return mock;
		}

	}

}
