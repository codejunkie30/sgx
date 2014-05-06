package com.wmsi.sgx.service.sandp.capiq;

import java.io.IOException;
import java.text.ParseException;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.service.sandp.capiq.impl.KeyDevResponseParser;

public class KeyDevsResponseParserTest{

	private KeyDevResponseParser keyDevResponseParser = new KeyDevResponseParser();
	
	@Test
	public void testConvert() throws ResponseParserException, JsonParseException, JsonMappingException, IOException, ParseException{
		
		KeyDevs devs = keyDevResponseParser.convert(KeyDevsTestUtils.getResponse());		
		KeyDevsTestUtils.verify(devs);
		
	}
}
