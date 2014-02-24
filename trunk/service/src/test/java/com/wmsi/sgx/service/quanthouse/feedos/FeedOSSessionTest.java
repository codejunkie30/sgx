package com.wmsi.sgx.service.quanthouse.feedos;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.feedos.api.core.Session;
import com.wmsi.sgx.config.quanthouse.feedos.FeedOSTestConfig;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@ContextConfiguration(classes = { FeedOSTestConfig.class })
public class FeedOSSessionTest extends AbstractTestNGSpringContextTests{

	@Autowired
	FeedOSConfig config;

	@Test(groups = { "functional", "integration" })
	public void getPriceDataTest() throws QuanthouseServiceException{
		FeedOSSession session = new FeedOSSession();
		session.setFeedOSconfig(config);

		Session sess = session.open();
		assertNotNull(sess);		
	}
	
	@Test(
		groups = { "functional", "integration" },
		expectedExceptions={QuanthouseServiceException.class}
	)
	public void testCantConnect() throws QuanthouseServiceException{
		FeedOSConfig conf = new FeedOSConfig();
		conf.setSessionName("Bad url");
		conf.setUrl("127.0.0.1");
		conf.setPort(6040);
		conf.setUser("bad");
		conf.setPassword("none");
		
		FeedOSSession session = new FeedOSSession();
		session.setFeedOSconfig(conf);
		session.open();
	}
	
	@Test(groups = { "functional", "integration" })
	public void testCloseSession() throws QuanthouseServiceException{
		FeedOSSession session = new FeedOSSession();
		session.setFeedOSconfig(config);
		
		Session ses = session.open();
		assertTrue(ses.isOpened());
		
		session.close();
		assertFalse(ses.isOpened());
	}
}
