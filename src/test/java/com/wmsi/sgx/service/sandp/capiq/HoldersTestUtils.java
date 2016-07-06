package com.wmsi.sgx.service.sandp.capiq;

import static org.testng.Assert.*;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.TestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.util.test.TestUtils;

public class HoldersTestUtils{

	public static CapIQResponse getResponse() {
		try{
			ObjectMapper mapper = TestUtils.getObjectMapper();
			Resource json = new ClassPathResource("data/capiq/holdersResponse.json");
			return mapper.readValue(json.getInputStream(), CapIQResponse.class);
		}
		catch(IOException e){
			throw new TestException("Failed to intialize financials object mapper", e);
		}
	}
	
	public static void verify(Holders holders){
		assertNotNull(holders);
		assertEquals(holders.getHolders().size(), 10);
		
		assertEquals(holders.getHolders().get(0).getName(), "Sun, Liping");
		assertEquals(holders.getHolders().get(0).getPercent(), 61.67935);
		assertEquals(holders.getHolders().get(0).getShares(), new Long(329152241));
		
		assertEquals(holders.getHolders().get(4).getName(), "Vantagepoint Investment Advisers, LLC");
		assertEquals(holders.getHolders().get(4).getPercent(), 1.11644);
		assertEquals(holders.getHolders().get(4).getShares(), new Long(5957900));

		
		assertEquals(holders.getHolders().get(9).getName(), "CIMB-Principal Asset Management Bhd");
		assertEquals(holders.getHolders().get(9).getPercent(), 0.47690);
		assertEquals(holders.getHolders().get(9).getShares(), new Long(2545000));

	}

}
