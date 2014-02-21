package com.wmsi.sgx.service.quanthouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.AppConfig;

@ContextConfiguration(classes={AppConfig.class})
public class QuanthouseServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	QuanthouseService service;
	
	@Test
	public void testLastPrice() throws QuanthouseServiceException, InvalidInstrumentException{
		Double price = service.getLastPrice("XSES", "C6L");
		Assert.assertNotNull(price);		
		System.out.println(price);	
	}
	
	@Test(expectedExceptions = {InvalidInstrumentException.class})
	public void testBadInstrumentCode() throws QuanthouseServiceException{
			service.getLastPrice("XSES", "aa");
	}	
}
