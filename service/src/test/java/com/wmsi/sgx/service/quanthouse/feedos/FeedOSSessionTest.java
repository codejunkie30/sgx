package com.wmsi.sgx.service.quanthouse.feedos;

import static org.testng.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.feedos.api.core.Session;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class FeedOSSessionTest extends AbstractTestNGSpringContextTests{

	@Configuration
	@ComponentScan(basePackageClasses = {FeedOSSession.class})
	@PropertySources(value = {@PropertySource("classpath:META-INF/properties/application.properties")})
	static class FeedOSTestConfig{

		@Autowired
		public Environment env;

		@Bean
		public FeedOSConfig feedOSConfig() {
			FeedOSConfig config = new FeedOSConfig();
			config.setSessionName(env.getProperty("quanthouse.api.sessionName"));
			config.setUrl(env.getProperty("quanthouse.api.url"));
			config.setPort(env.getProperty("quanthouse.api.port", Integer.class));
			config.setUser(env.getProperty("quanthouse.api.user"));
			config.setPassword(env.getProperty("quanthouse.api.password"));
			return config;
		}
	}

	@Autowired 
	FeedOSSession feedOSSession;
	
	@Test(groups = { "functional", "integration" })
	@DirtiesContext
	public void getPriceDataTest() throws QuanthouseServiceException{
		Session sess = feedOSSession.open();
		assertNotNull(sess);		
	}

	@Test(
		groups = { "functional", "integration" },
		expectedExceptions={QuanthouseServiceException.class}
	)
	@DirtiesContext
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
	@DirtiesContext
	public void testCloseSession() throws QuanthouseServiceException{
		Session ses = feedOSSession.open();
		assertTrue(ses.isOpened());
		
		feedOSSession.close();
		assertFalse(ses.isOpened());
	}
}
