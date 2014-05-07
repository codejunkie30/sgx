package com.wmsi.sgx.service.sandp.capiq;

import static org.testng.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.HttpConfig;
import com.wmsi.sgx.model.KeyDevs;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={HttpConfig.class})
public class CapIQServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	public CapIQService capIQService;
	
	@DataProvider
	Object[][] testTickers(){
		String date = "03/10/2014";
		
		return new Object[][]{
				{"C6L", date}, 
				{"A7S", date}
		};
	}
	
	@Test(dataProvider="testTickers")
	public void testGetCompanyInfo(String ticker, String date) throws CapIQRequestException, ResponseParserException{
		capIQService.getCompanyInfo(ticker, date);
	}
	
	@Test(dataProvider="testTickers")
	public void testGetCompanyFinancials(String ticker, String date) throws CapIQRequestException, ResponseParserException{
		capIQService.getCompanyFinancials(ticker, "SGD");
	}
	
	@Test(dataProvider="testTickers")
	public void testGetHistoricalData(String ticker, String date) throws CapIQRequestException, ResponseParserException{
		capIQService.getHistoricalData(ticker, date);
	}

	@Test(dataProvider="testTickers")
	public void testGetKeyDevs(String ticker, String date) throws CapIQRequestException, ResponseParserException{
		KeyDevs keyDevs = capIQService.getKeyDevelopments(ticker, date);
		assertNotNull(keyDevs);		
		assertNotNull(keyDevs.getKeyDevs());		
	}
	
}
