package com.wmsi.sgx.service.quanthouse;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.AppConfig;
import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSData;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSService;

@ContextConfiguration(classes={AppConfig.class})
public class QuanthouseServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	QuanthouseService service;

	FeedOSService mockFeedOSService;
	
	@BeforeClass
	public void init() throws ParseException, QuanthouseServiceException{
		// Mock service
		mockFeedOSService = mock(FeedOSService.class);
		service.setFeedOSService(mockFeedOSService);
		
		FeedOSData data = new FeedOSData();
		data.setClosePrice(10.17);
		data.setLastPrice(10.14);
		data.setOpenPrice(10.20);		
		data.setCurrentBusinessDay(new SimpleDateFormat("MM/dd/yyyy").parse("02/24/2014"));
		data.setPreviousBusinessDay(new SimpleDateFormat("MM/dd/yyyy").parse("02/21/2014"));
		
		stub(mockFeedOSService.getPriceData(anyString(), anyString())).toReturn(data);
	}
	
	@Test
	public void testPrice() throws QuanthouseServiceException, InvalidInstrumentException{
		Price price = service.getPrice("XSES", "C6L");
		
		assertNotNull(price);	
		assertEquals(price.getPercentChange(), -0.29D);
		assertEquals(price.getChange(), -0.03D);
	}
	
	@Test(groups={"functional"}) 
	public void testPriceCache() throws QuanthouseServiceException{
		String market = "XSES";
		String id = "C6L";
		
		// Invoke method twice, should only hit once. 
		service.getPrice(market, id);
		service.getPrice(market, id);

		// Make sure the method was only called once
		verify(mockFeedOSService, times(1)).getPriceData("XSES", "C6L_RY");
	}
}
