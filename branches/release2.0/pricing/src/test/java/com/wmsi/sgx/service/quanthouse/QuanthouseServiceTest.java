package com.wmsi.sgx.service.quanthouse;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSConfig;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSData;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSService;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSSession;
import com.wmsi.sgx.service.quanthouse.impl.QuanthouseServiceImpl;
import com.wmsi.sgx.test.TestUtils;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class QuanthouseServiceTest extends AbstractTestNGSpringContextTests{

	@Configuration
	@ComponentScan(basePackageClasses = { QuanthouseServiceImpl.class })
	@EnableCaching
	static class ContextConfiguration{

		@Bean(name = "feedOSService")
		public FeedOSService feedOSService() {
			return mock(FeedOSService.class);
		}

		@Bean
		public FeedOSSession feedOSSession() {
			return mock(FeedOSSession.class);
		}

		@Bean
		public FeedOSConfig feedOSConfig() {
			return mock(FeedOSConfig.class);
		}

		@Bean
		public CacheManager cacheManager(){
			return TestUtils.createSimpleCacheManager(60, 10);
		}
	}

	@Autowired
	QuanthouseService service;

	@Autowired
	FeedOSService mockFeedOSService;

	private String market = "XSES";
	private String id = "C6L";
	/*
	@BeforeMethod
	public void init() throws ParseException, QuanthouseServiceException {

		// Create mock feed data object to return 
		FeedOSData data = new FeedOSData();
		data.setClosePrice(10.17);
		data.setLastPrice(10.14);
		data.setOpenPrice(10.20);
		data.setCurrentBusinessDay(new SimpleDateFormat("MM/dd/yyyy").parse("02/24/2014"));
		data.setPreviousBusinessDay(new SimpleDateFormat("MM/dd/yyyy").parse("02/21/2014"));

		// Mock get price data object to return test FeedOSData for any strings
		stub(mockFeedOSService.getPriceData(anyString(), anyString())).toReturn(data);
	}

	@Test
	public void testPrice() throws QuanthouseServiceException,	InvalidInstrumentException {

		Price price = service.getPrice(market, id);

		assertNotNull(price);
		assertEquals(price.getPercentChange(), -0.295D);
		assertEquals(price.getChange(), -0.03D);
	}

	@Test
	@DirtiesContext
	public void testLiquidatedTicker() throws ParseException, QuanthouseServiceException{
		
		// Expired tickers should still have a close price, but no last price or open price 
		FeedOSData data = new FeedOSData();
		data.setClosePrice(10.17);
		data.setCurrentBusinessDay(new SimpleDateFormat("MM/dd/yyyy").parse("02/24/2014"));
		data.setPreviousBusinessDay(new SimpleDateFormat("MM/dd/yyyy").parse("02/21/2014"));
		
		stub(mockFeedOSService.getPriceData(anyString(), anyString())).toReturn(data);
		
		Price p = service.getPrice(market, "OldTicker");
		assertNotNull(p);
	}

	@Test 
	@DirtiesContext
	public void testLastTradeDate_offBookBefore() throws ParseException, QuanthouseServiceException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		String lastTradeTime = "02/24/2014 09:16:13";
		String offBookTime = "02/24/2014 14:22:42";
		
		FeedOSData data = new FeedOSData();
		data.setClosePrice(10.17);
		data.setLastTradeTimestamp(sdf.parse(lastTradeTime));
		data.setLastOffBookTradeTimestamp(sdf.parse(offBookTime));
		
		stub(mockFeedOSService.getPriceData(anyString(), anyString())).toReturn(data);
		
		Price p = service.getPrice(market, id);		
		assertNotNull(p);
		assertEquals(p.getLastTradeTimestamp(), sdf.parse(offBookTime));		
	}

	@Test 
	@DirtiesContext
	public void testLastTradeDate_offBookAfter() throws ParseException, QuanthouseServiceException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		String lastTradeTime = "02/24/2014 15:16:13";
		String offBookTime = "02/24/2014 14:22:42";
		
		FeedOSData data = new FeedOSData();
		data.setClosePrice(10.17);
		data.setLastTradeTimestamp(sdf.parse(lastTradeTime));
		data.setLastOffBookTradeTimestamp(sdf.parse(offBookTime));
		
		stub(mockFeedOSService.getPriceData(anyString(), anyString())).toReturn(data);
		
		Price p = service.getPrice(market, id);		
		assertNotNull(p);
		assertEquals(p.getLastTradeTimestamp(), sdf.parse(lastTradeTime));		
	}

	@Test 
	@DirtiesContext
	public void testLastTradeDate_offBookNull() throws ParseException, QuanthouseServiceException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		String lastTradeTime = "02/24/2014 15:16:13";
		
		FeedOSData data = new FeedOSData();
		data.setClosePrice(10.17);
		data.setLastTradeTimestamp(sdf.parse(lastTradeTime));
		
		stub(mockFeedOSService.getPriceData(anyString(), anyString())).toReturn(data);
		
		Price p = service.getPrice(market, id);		
		assertNotNull(p);
		assertEquals(p.getLastTradeTimestamp(), sdf.parse(lastTradeTime));		
	}

	@Test 
	@DirtiesContext
	public void testLastTradeDate_lastTradeNull() throws ParseException, QuanthouseServiceException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		String offBookTime = "02/24/2014 14:22:42";
		
		FeedOSData data = new FeedOSData();
		data.setClosePrice(10.17);
		data.setLastOffBookTradeTimestamp(sdf.parse(offBookTime));
		
		stub(mockFeedOSService.getPriceData(anyString(), anyString())).toReturn(data);
		
		Price p = service.getPrice(market, id);		
		assertNotNull(p);
		assertEquals(p.getLastTradeTimestamp(), sdf.parse(offBookTime));		
	}

	@Test 
	@DirtiesContext
	public void testLastTradeDate_lastTradeTimesNull() throws ParseException, QuanthouseServiceException{
		
		FeedOSData data = new FeedOSData();
		data.setClosePrice(10.17);
		
		stub(mockFeedOSService.getPriceData(anyString(), anyString())).toReturn(data);
		
		Price p = service.getPrice(market, id);		
		assertNotNull(p);
		assertNull(p.getLastTradeTimestamp());
	}

	@Test(groups = { "functional" })
	public void testPriceCache() throws QuanthouseServiceException {

		// Invoke method twice, should only hit once .
		service.getPrice(market, id);
		service.getPrice(market, id);

		// Make sure the method was only called once
		verify(mockFeedOSService, times(1)).getPriceData(market, id.concat("_RY"));
	}
*/	
	
}
