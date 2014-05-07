package com.wmsi.sgx.service.sandp.capiq;

import static org.testng.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.HttpConfig;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class CapIQRequestExecutorIntegrationTest extends AbstractTestNGSpringContextTests{

	@Configuration
	//@ComponentScan(basePackageClasses = { CapIQServiceImpl.class})
	@Import(HttpConfig.class)
	static class CapIqServiceTestConfig{}
	
	@Autowired
	CapIQRequestExecutor capIqRequestExecutor;
	
	@Test(groups={"functional", "integration"})
	public void testValidRequest() throws CapIQRequestException{
		
		String validQuery = "{\"inputRequests\":[{\"function\":\"GDSP\", \"identifier\":\"$id$\",\"mnemonic\":\"IQ_MARKETCAP\"}]}";
		
		Map<String, Object> ctx = new HashMap<String,Object>();
		ctx.put("id", "IBM");
		Resource template = new ByteArrayResource(validQuery.getBytes());
		
		CapIQResponse response = capIqRequestExecutor.execute(new CapIQRequest(template), ctx);
		
		assertNotNull(response);
		assertEquals(response.getResults().size(), 1);
		assertNotNull(response.getResults().get(0));		
		assertNull(response.getResults().get(0).getErrorMsg());
		assertEquals(response.getResults().get(0).getMnemonic(), "IQ_MARKETCAP");
		assertEquals(response.getResults().get(0).getIdentifier(), "IBM");
	}

	@Test(groups={"functional", "integration"})
	public void testInvalidId() throws CapIQRequestException{
		String invalidQuery = "{\"inputRequests\":[{\"function\":\"GDSP\", \"identifier\":\"$id$\",\"mnemonic\":\"IQ_MARKETCAP\"}]}";
		
		Map<String, Object> ctx = new HashMap<String,Object>();
		ctx.put("id", "ffffffffff");
		Resource template = new ByteArrayResource(invalidQuery.getBytes());
		
		CapIQResponse response = capIqRequestExecutor.execute(new CapIQRequest(template), ctx);
		
		assertNotNull(response);
		assertNull(response.getErrorMsg());
		assertNotNull(response.getResults());
		assertNotNull(response.getResults().get(0).getErrorMsg());
	}

	@Test(groups={"functional", "integration"})
	public void testErrorMsgResponse() throws CapIQRequestException{
		
		Resource template = new ByteArrayResource(new byte[]{});
		CapIQResponse response = capIqRequestExecutor.execute(new CapIQRequest(template), null);
		assertNotNull(response);
		assertNotNull(response.getErrorMsg());
		assertNull(response.getResults());
	}
}
