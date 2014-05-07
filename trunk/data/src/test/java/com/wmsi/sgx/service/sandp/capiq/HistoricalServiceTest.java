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
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.HistoricalService;
import com.wmsi.sgx.util.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class HistoricalServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private HistoricalService historicalService;
	
	SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
	
	@Test
	public void testLoadHistory() throws CapIQRequestException, ParseException, ResponseParserException{
		PriceHistory history = historicalService.load("A7S", "05/06/2013");
		
		assertNotNull(history);
		
		assertNotNull(history.getPrice());
		assertEquals(history.getPrice().size(), 253);
		

		HistoricalValue priceFirst = history.getPrice().get(0);
		assertEquals(priceFirst.getValue(), 0.455D);
		assertEquals(priceFirst.getDate(), fmt.parse("05/06/2013"));

		HistoricalValue priceMiddle = history.getPrice().get(126);
		assertEquals(priceMiddle.getValue(), 0.435D);
		assertEquals(priceMiddle.getDate(), fmt.parse("11/04/2013"));

		HistoricalValue priceLast = history.getPrice().get(252);
		assertEquals(priceLast.getValue(), 0.48D);
		assertEquals(priceLast.getDate(), fmt.parse("05/06/2014"));

		assertNotNull(history.getVolume());
		assertEquals(history.getVolume().size(), 253);

		HistoricalValue volumeFirst = history.getVolume().get(0);
		assertEquals(volumeFirst.getValue(), 1.047000D);
		assertEquals(volumeFirst.getDate(), fmt.parse("05/06/2013"));

		HistoricalValue volumeMiddle = history.getVolume().get(126);
		assertEquals(volumeMiddle.getValue(), 0.317D);
		assertEquals(volumeMiddle.getDate(), fmt.parse("11/04/2013"));

		HistoricalValue volumeLast = history.getVolume().get(252);
		assertEquals(volumeLast.getValue(), 2.099D);
		assertEquals(volumeLast.getDate(), fmt.parse("05/06/2014"));
		
	}
	
	@Configuration
	static class HistoricalServiceTestConfig{

		private ObjectMapper mapper = TestUtils.getObjectMapper();
		
		@Bean
		public HistoricalService historicalService() throws JsonParseException, JsonMappingException, CapIQRequestException, IOException{
			HistoricalService serv = new HistoricalService();
			serv.setRequestExecutor(requestExecutor());
			return serv;
		}
		
		@Bean
		@SuppressWarnings("unchecked")
		public CapIQRequestExecutor requestExecutor() throws CapIQRequestException, JsonParseException, JsonMappingException, IOException{
			
			Resource json = new ClassPathResource("data/capiq/priceHistoryY1.json");
			CapIQResponse response = mapper.readValue(json.getInputStream(), CapIQResponse.class);

			CapIQRequestExecutor mock = mock(CapIQRequestExecutor.class);
			when(mock.execute( any(CapIQRequest.class), anyMap()) )
				.thenReturn(response);

			return mock;
		}

	}

}
