package com.wmsi.sgx.service.sandp.capiq;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.KeyDevsService;
import com.wmsi.sgx.util.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class KeyDevsServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private KeyDevsService keyDevsService; 
	
	SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	
	@Test
	public void testLoadKeyDevs() throws ResponseParserException, CapIQRequestException, ParseException{
		KeyDevs devs = keyDevsService.loadKeyDevelopments("A7S", "05/06/2014");
		
		assertNotNull(devs);
		assertEquals(devs.getTickerCode(), "A7S");
		
		assertNotNull(devs);
		assertNotNull(devs.getKeyDevs());
		assertEquals(devs.getKeyDevs().size(), 2);
		
		assertNotNull(devs.getKeyDevs().get(0));
		KeyDev dev1 = devs.getKeyDevs().get(0);
		assertEquals(dev1.getDate(), fmt.parse("05/06/2014 09:09:00") );
		assertEquals(dev1.getHeadline(), "800 Super Holdings Limited Promotes Tan Kelly as Financial Controller, with Effect on 7 May 2014");
		assertEquals(dev1.getSituation(), "800 Super Holdings Limited announced that Ms Tan Kelly is promoted as the financial controller with effect on 7 May 2014 pursuant to the resignation of Mr. Teo Theng How, on 6 May 2014.");
		
		
		assertNotNull(devs.getKeyDevs().get(1));
		KeyDev dev2 = devs.getKeyDevs().get(1);
		assertEquals(dev2.getDate(), fmt.parse("05/06/2014 09:07:00") );
		assertEquals(dev2.getHeadline(), "800 Super Holdings Limited Announces Cessation of Teo Theng How as Financial Controller");
		assertEquals(dev2.getSituation(), "800 Super Holdings Limited announced the cessation of Teo Theng How as Financial Controller effective May 6, 2014.");

	}
	
	@Configuration
	static class KeyDevsServiceTestConfig{

		private ObjectMapper mapper = TestUtils.getObjectMapper();
		
		@Bean
		public KeyDevsService keyDevsService(){
			return new KeyDevsService();
		}
		
		@Bean
		@SuppressWarnings("unchecked")
		public CapIQRequestExecutor requestExecutor() throws CapIQRequestException, JsonParseException, JsonMappingException, IOException{
			
			Resource json = new ClassPathResource("data/capiq/keyDevsResponse.json");
			CapIQResponse response = mapper.readValue(json.getInputStream(), CapIQResponse.class);

			CapIQRequestExecutor mock = mock(CapIQRequestExecutor.class);
			when(mock.execute( any(CapIQRequest.class), anyMap()) )
				.thenReturn(response);
			
			when(mock.execute( any(String.class) ) )
				.thenReturn(response);

			return mock;
		}
		
	}
	
}
