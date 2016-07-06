package com.wmsi.sgx.service.sandp.capiq;

import java.io.IOException;
import java.text.ParseException;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.service.sandp.capiq.impl.FinancialsResponseParser;

public class FinancialsResponseParserTest {

	private FinancialsResponseParser financialsResponseParser = new FinancialsResponseParser();
	
	//@Test
	public void testConvert() throws ResponseParserException, JsonParseException, JsonMappingException, IOException, ParseException{
		
		Financials financials = financialsResponseParser.convert(FinancialsTestUtils.getFinancialsResponse());
		FinancialsTestUtils.verify(financials);
		
	}
}
