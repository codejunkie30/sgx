package com.wmsi.sgx.service.sandp.capiq;

import org.testng.annotations.Test;

import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.service.sandp.capiq.impl.HoldersResponseParser;

public class HoldersResponseParserTest{

	private HoldersResponseParser holdersResponseParser = new HoldersResponseParser();
	
	@Test
	public void testConvert() throws ResponseParserException {
		
		Holders holders = holdersResponseParser.convert(HoldersTestUtils.getResponse());
		HoldersTestUtils.verify(holders);
		
	}
}
