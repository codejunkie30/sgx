package com.wmsi.sgx.service.quanthouse.feedos;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.quanthouse.feedos.FeedOSTestConfig;
import com.wmsi.sgx.service.quanthouse.InvalidInstrumentException;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@ContextConfiguration(classes={FeedOSTestConfig.class})
public class FeedOSServiceTest  extends AbstractTestNGSpringContextTests{

	@Autowired
	FeedOSConfig config;
	
	FeedOSService service;

	@BeforeClass
	public void init(){
		FeedOSSession session = new FeedOSSession();
		session.setFeedOSconfig(config);
		service = new FeedOSService();
		service.setFeedOSSession(session);
	}
	
	@Test(groups={"functional", "integration"})
	public void getPriceDataTest() throws QuanthouseServiceException{
		FeedOSData data = service.getPriceData("XSES", "C6L_RY");
		assertNotNull(data);
		assertNotNull(data.getLastPrice());
		assertNotNull(data.getOpenPrice());
		assertNotNull(data.getClosePrice());
		assertNotNull(data.getPreviousBusinessDay());
		assertNotNull(data.getCurrentBusinessDay());
	}
	
	@Test(expectedExceptions = {InvalidInstrumentException.class})
	public void testBadInstrumentCode() throws QuanthouseServiceException{
		service.getPriceData("XSES", "aa");
	}	
}
