package com.wmsi.sgx.service.sandp.capiq;

import static org.testng.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.TestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.util.test.TestUtils;

public class CompanyTestUtils{

	public static CapIQResponse getCompanyResponse(){
		try{
			ObjectMapper mapper = TestUtils.getObjectMapper();
			Resource json = new ClassPathResource("data/capiq/companyResponse.json");
			return mapper.readValue(json.getInputStream(), CapIQResponse.class);
		}
		catch(IOException e){
			throw new TestException("Failed to intialize company object mapper", e );
		}
	}
	
	public static void verify(Company company) throws ParseException{
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		
		assertEquals(company.getYearLow(), 0.39);
		assertEquals(company.getBeta5Yr(), 1.54827406481951D);
		assertEquals(company.getIndustryGroup(), "Materials");
		assertEquals(company.getIndustry(), "Chemicals");		
		assertEquals(company.getTickerCode(), "A7S");		
		assertEquals(company.getPreviousCloseDate(), fmt.parse("2014-05-05"));
		assertEquals(company.getYearFounded(), new Integer(2004));
		assertNull(company.getDividendYield());
		assertNull(company.getAvgBrokerReq());	
		assertNull(company.getCashInvestments());
	}

}
