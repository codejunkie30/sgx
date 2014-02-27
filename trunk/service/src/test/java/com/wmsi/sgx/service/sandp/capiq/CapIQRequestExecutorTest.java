package com.wmsi.sgx.service.sandp.capiq;

import static org.testng.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.HttpConfig;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class CapIQRequestExecutorTest extends AbstractTestNGSpringContextTests{

	@Configuration
	@ComponentScan(basePackageClasses = { CapIQServiceImpl.class})
	@Import(HttpConfig.class)
	static class CapIqServiceTestConfig{}
	
	@Autowired
	CapIQRequestExecutor executor;
	
	@Test(groups={"functional", "integration"})
	public void testValidRequest() throws CapIQRequestException{
		String validQuery = "{\"inputRequests\":[{\"function\":\"GDSP\", \"identifier\":\"IBM\",\"mnemonic\":\"IQ_MARKETCAP\"}]}";
		CapIQResponse response = executor.execute(validQuery);
		
		assertNotNull(response);
		assertEquals(response.getResults().size(), 1);
		assertNotNull(response.getResults().get(0));		
		assertNull(response.getResults().get(0).getErrorMsg());
		assertEquals(response.getResults().get(0).getMnemonic(), "IQ_MARKETCAP");
		assertEquals(response.getResults().get(0).getIdentifier(), "IBM");
	}

	@Test(groups={"functional", "integration"})
	public void testInvalidId() throws CapIQRequestException{
		String invalidQuery = "{\"inputRequests\":[{\"function\":\"GDSP\", \"identifier\":\"ffffffffff\",\"mnemonic\":\"IQ_MARKETCAP\"}]}";
		CapIQResponse response = executor.execute(invalidQuery);
		
		assertNotNull(response);
		assertNull(response.getErrorMsg());
		assertNotNull(response.getResults());
		assertNotNull(response.getResults().get(0).getErrorMsg());
	}

	@Test(groups={"functional", "integration"})
	public void testErrorMsgResponse() throws CapIQRequestException{
		CapIQResponse response = executor.execute(null);
		assertNotNull(response);
		assertNotNull(response.getErrorMsg());
		assertNull(response.getResults());
	}

	@Test(groups={"functional", "integration"},
			expectedExceptions={CapIQRequestException.class})	
	public void testJsonDeserializationResponse() throws CapIQRequestException{
		String badJsonQuery = "{\"inputRequests\":[{ \"mnemonicSpelledWrong\":\"IQ_MARKETCAP\"}]}";;
		CapIQResponse response = executor.execute(badJsonQuery);
		
		assertNotNull(response);
		assertNotNull(response.getErrorMsg());
	}	
}
