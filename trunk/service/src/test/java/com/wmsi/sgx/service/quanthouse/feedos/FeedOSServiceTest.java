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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.wmsi.sgx.service.quanthouse.InvalidInstrumentException;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class FeedOSServiceTest extends AbstractTestNGSpringContextTests{


	@Configuration
	@ComponentScan(basePackageClasses = {FeedOSSession.class, FeedOSService.class})
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
	FeedOSService feedOSService;

	@Test(groups = { "functional", "integration" })
	public void getPriceDataTest() throws QuanthouseServiceException {
		FeedOSData data = feedOSService.getPriceData("XSES", "C6L_RY");
		assertNotNull(data);
		assertNotNull(data.getLastPrice());
		assertNotNull(data.getOpenPrice());
		assertNotNull(data.getClosePrice());
		assertNotNull(data.getPreviousBusinessDay());
		assertNotNull(data.getCurrentBusinessDay());
	}

	@Test(expectedExceptions = { InvalidInstrumentException.class })
	public void testBadInstrumentCode() throws QuanthouseServiceException {
		feedOSService.getPriceData("XSES", "aa");
	}
	
	@Test(groups = { "functional", "integration" })
	public void testExpiredInstrument() throws QuanthouseServiceException {
		FeedOSData priceData = feedOSService.getPriceData("XSES", "E25_RY");
		assertNotNull(priceData);
		assertNotNull(priceData.getClosePrice());
		assertNotNull(priceData.getCurrentBusinessDay());
		assertNotNull(priceData.getPreviousBusinessDay());
	}

}
