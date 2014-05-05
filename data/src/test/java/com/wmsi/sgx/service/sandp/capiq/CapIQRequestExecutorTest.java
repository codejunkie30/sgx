package com.wmsi.sgx.service.sandp.capiq;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.HashMap;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.HttpConfig;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={HttpConfig.class})
public class CapIQRequestExecutorTest extends AbstractTestNGSpringContextTests{
	
	@Autowired
	@Qualifier("capIqRestTemplate")
	RestTemplate capIqRestTemplate;
			
	CapIQRequestExecutor executor;
	MockRestServiceServer mockServer;
	
	@BeforeMethod
	public void init(){
		executor = new CapIQRequestExecutor();
		executor.setRestTemplate(capIqRestTemplate);
		executor.setUrl("/fakeSite");
		mockServer = MockRestServiceServer.createServer(capIqRestTemplate);
	}

	@Test
	public void testValidRequest() throws CapIQRequestException{

		mockServer.expect(requestTo("/fakeSite"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withSuccess("{\"test\":2}", MediaType.APPLICATION_JSON));
		
		Resource template = new ByteArrayResource(new byte[]{});
		executor.execute(new CapIQRequest(template), null);
	}

	@Test(expectedExceptions={CapIQRequestException.class})
	public void testBadRequest() throws CapIQRequestException{
		mockServer.expect(requestTo("/fakeSite"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withBadRequest());
		
		Resource template = new ByteArrayResource(new byte[]{});
		executor.execute(new CapIQRequest(template), null);
	}

	@Test(expectedExceptions={CapIQRequestException.class})
	public void test500Error() throws CapIQRequestException{
		mockServer.expect(requestTo("/fakeSite"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withServerError());
		
		Resource template = new ByteArrayResource(new byte[]{});
		executor.execute(new CapIQRequest(template), null);
	}
	
	/**
	 * Test real example of response from capIQ api
	 */
	@Test(groups={"functional"})
	public void testRealJsonRequest() throws CapIQRequestException{
		String goodResponse = 
			"{\"GDSSDKResponse\":[{\"Headers\": [\"IQ_MARKETCAP\"],\"Rows\":[{\"Row\": [\"191669.224460\"]}]}]}";

		mockServer.expect(requestTo("/fakeSite"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withSuccess(goodResponse, MediaType.APPLICATION_JSON));
		
		Resource template = new ByteArrayResource(new byte[]{});
		executor.execute(new CapIQRequest(template), null);
	}

	/**
	 * Test json response who's type has unexpectedly changed. 
	 * Json is valid, but 'Headers' field has changed type to an array from a String.
	 * This happens in the capIQ api when a bad parameter name is passed,
	 * eg. if "mnemonic" were spelled incorrectly Header would be an array in the response
	 * where as it's a String any other time. While we could code around this particular case 
	 * with Jackson, there's know way of knowing what other fields may change now, or in the future.
	 * This test case ensure's we'll bubble up the proper exception if we hit unexpected json types
	 */
	@Test(
		groups={"functional"},
		expectedExceptions={CapIQResponseConversionException.class}
		)
	public void testUnexpectedJsonFormatRequest() throws CapIQRequestException{

		String validButUnexpectedResponse = 
			"{\"GDSSDKResponse\":[{\"Mnemonic\": [\"IQ_MARKETCAP\"],\"Rows\":[{\"Row\": [\"191669.224460\"]}]}]}";

		mockServer.expect(requestTo("/fakeSite"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withSuccess(validButUnexpectedResponse, MediaType.APPLICATION_JSON));
		
		Resource template = new ByteArrayResource(new byte[]{});
		executor.execute(new CapIQRequest(template), null);
	}

}
