package com.wmsi.sgx.service.sandp.capiq;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestImpl;
import com.wmsi.sgx.util.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class CapIQRequestExecutorTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private RestTemplate capIqRestTemplate;

	private CapIQRequestExecutor executor;
	private MockRestServiceServer mockServer;

	@BeforeMethod
	public void init() {
		executor = new CapIQRequestExecutor();
		executor.setRestTemplate(capIqRestTemplate);
		executor.setUrl("/fakeSite");
		mockServer = MockRestServiceServer.createServer(capIqRestTemplate);
	}

	@Test
	public void testValidRequest() throws CapIQRequestException {

		mockServer.expect(requestTo("/fakeSite")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess("{\"test\":2}", MediaType.APPLICATION_JSON));

		Resource template = new ByteArrayResource(new byte[] {});
		executor.execute(new CapIQRequestImpl(template), null);
	}

	@Test(expectedExceptions = { CapIQRequestException.class })
	public void testBadRequest() throws CapIQRequestException {
		mockServer.expect(requestTo("/fakeSite"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withBadRequest());

		Resource template = new ByteArrayResource(new byte[] {});
		executor.execute(new CapIQRequestImpl(template), null);
	}

	@Test(expectedExceptions = { CapIQRequestException.class })
	public void test500Error() throws CapIQRequestException {
		mockServer.expect(requestTo("/fakeSite"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withServerError());

		Resource template = new ByteArrayResource(new byte[] {});
		executor.execute(new CapIQRequestImpl(template), null);
	}

	/**
	 * Test real example of response from capIQ api
	 */
	@Test(groups = { "functional" })
	public void testRealJsonRequest() throws CapIQRequestException {
		String goodResponse = "{\"GDSSDKResponse\":[{\"Headers\": [\"IQ_MARKETCAP\"],\"Rows\":[{\"Row\": [\"191669.224460\"]}]}]}";

		mockServer.expect(requestTo("/fakeSite"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withSuccess(goodResponse, MediaType.APPLICATION_JSON));

		Resource template = new ByteArrayResource(new byte[] {});
		executor.execute(new CapIQRequestImpl(template), null);
	}

	/**
	 * Test json response who's type has unexpectedly changed. Json is valid,
	 * but 'Headers' field has changed type to an array from a String. This
	 * happens in the capIQ api when a bad parameter name is passed, eg. if
	 * "mnemonic" were spelled incorrectly Header would be an array in the
	 * response where as it's a String any other time. While we could code
	 * around this particular case with Jackson, there's know way of knowing
	 * what other fields may change now, or in the future. This test case
	 * ensure's we'll bubble up the proper exception if we hit unexpected json
	 * types
	 */
	@Test(groups = { "functional" }, expectedExceptions = { CapIQResponseConversionException.class })
	public void testUnexpectedJsonFormatRequest() throws CapIQRequestException {

		String validButUnexpectedResponse = "{\"GDSSDKResponse\":[{\"Mnemonic\": [\"IQ_MARKETCAP\"],\"Rows\":[{\"Row\": [\"191669.224460\"]}]}]}";

		mockServer.expect(requestTo("/fakeSite")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(validButUnexpectedResponse, MediaType.APPLICATION_JSON));

		Resource template = new ByteArrayResource(new byte[] {});
		executor.execute(new CapIQRequestImpl(template), null);
	}

	@Configuration
	static class CapIQRequestExecutorTestConfig{
		@Bean(name = "capIqJsonMessageConverter")
		public MappingJackson2HttpMessageConverter capIqJsonConverter() {
			MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
			jackson.setSupportedMediaTypes(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			jackson.setObjectMapper(TestUtils.getObjectMapper());
			return jackson;
		}

		@Bean(name = "capIqRestTemplate")
		public RestTemplate capIqRestTemplate() {
			RestTemplate template = new RestTemplate();

			List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
			converters.add(new FormHttpMessageConverter());
			converters.add(capIqJsonConverter());
			template.setMessageConverters(converters);

			return template;
		}
	}

}
