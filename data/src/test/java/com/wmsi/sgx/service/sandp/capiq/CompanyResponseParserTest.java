package com.wmsi.sgx.service.sandp.capiq;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.CompanyResponseParser;
import com.wmsi.sgx.util.test.TestUtils;

public class CompanyResponseParserTest{

	private CompanyResponseParser companyResponseParser = new CompanyResponseParser();	
	private ObjectMapper mapper = TestUtils.getObjectMapper();
	
	@Test
	public void testConvert() throws ResponseParserException, JsonParseException, JsonMappingException, IOException, ParseException{
	
		CapIQResponse response = CompanyTestUtils.getCompanyResponse();

		Company company = companyResponseParser.convert(response);
		CompanyTestUtils.verify(company);
	}	

	@Test(expectedExceptions=InvalidIdentifierException.class)
	public void testInvalidIdentifier() throws JsonParseException, JsonMappingException, IOException, ResponseParserException {
		Resource json = new ClassPathResource("data/capiq/invalidIdentifierResponse.json");
		CapIQResponse response = mapper.readValue(json.getInputStream(), CapIQResponse.class);		
		companyResponseParser.convert(response);
	}

	@Test(expectedExceptions=ResponseParserException.class, expectedExceptionsMessageRegExp="Error result in capIq request.*")
	public void testErrorResponse() throws JsonParseException, JsonMappingException, IOException, ResponseParserException {
		Resource json = new ClassPathResource("data/capiq/errorResponse.json");
		CapIQResponse response = mapper.readValue(json.getInputStream(), CapIQResponse.class);		
		companyResponseParser.convert(response);
	}
}
