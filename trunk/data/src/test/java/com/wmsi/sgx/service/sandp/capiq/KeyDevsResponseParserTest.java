package com.wmsi.sgx.service.sandp.capiq;

import static org.testng.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.KeyDevResponseParser;
import com.wmsi.sgx.util.test.TestUtils;

public class KeyDevsResponseParserTest{

	@Autowired
	private ObjectMapper mapper = TestUtils.getObjectMapper();

	private KeyDevResponseParser keyDevResponseParser = new KeyDevResponseParser();
	
	SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	
	@Test
	public void testConver() throws ResponseParserException, JsonParseException, JsonMappingException, IOException, ParseException{
		
		Resource json = new ClassPathResource("data/capiq/keyDevsResponse.json");
		CapIQResponse response = mapper.readValue(json.getInputStream(), CapIQResponse.class);

		KeyDevs devs = keyDevResponseParser.convert(response);
		assertNotNull(devs);
		assertNotNull(devs.getKeyDevs());
		assertEquals(devs.getKeyDevs().size(), 2);
		
		assertNotNull(devs.getKeyDevs().get(0));
		KeyDev dev1 = devs.getKeyDevs().get(0);
		assertEquals(dev1.getDate(), fmt.parse("05/06/2014 09:09:00") );
		assertEquals(dev1.getHeadline(), "800 Super Holdings Limited Promotes Tan Kelly as Financial Controller, with Effect on 7 May 2014");
		assertEquals(dev1.getSituation(), "800 Super Holdings Limited announced that Ms Tan Kelly is promoted as the financial controller with effect on 7 May 2014 pursuant to the resignation of Mr. Teo Theng How, on 6 May 2014.");
		
		
		assertNotNull(devs.getKeyDevs().get(1));
		KeyDev dev2 = devs.getKeyDevs().get(1);
		assertEquals(dev2.getDate(), fmt.parse("05/06/2014 09:07:00") );
		assertEquals(dev2.getHeadline(), "800 Super Holdings Limited Announces Cessation of Teo Theng How as Financial Controller");
		assertEquals(dev2.getSituation(), "800 Super Holdings Limited announced the cessation of Teo Theng How as Financial Controller effective May 6, 2014.");
	}
}
