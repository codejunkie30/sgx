package com.wmsi.sgx.service.sandp.capiq;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.HttpConfig;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class CapIQServiceTest extends AbstractTestNGSpringContextTests{


	@Configuration
	@ComponentScan(basePackageClasses = {CapIQServiceImpl.class})
	@Import(HttpConfig.class)
	static class CapIQServiceTestConfig{}
	
	@Autowired
	public CapIQService capIQService;
	
	@DataProvider
	Object[][] testTickers(){
		String date = "02/28/2014";
		
		return new Object[][]{
				{"C6L", date}, 
				{"A7S", date}
		};
	}
	
	@Test(dataProvider="testTickers")
	public void testGetCompanyInfo(String ticker, String date) throws CapIQRequestException{
		capIQService.getCompanyInfo(ticker, date);
	}
	
	@Test(dataProvider="testTickers")
	public void testGetCompanyFinancials(String ticker, String date) throws CapIQRequestException{
		capIQService.getCompanyFinancials(ticker, "LTM");
	}
	
	@Test(dataProvider="testTickers")
	public void testGetHistoricalData(String ticker, String date) throws CapIQRequestException{
		capIQService.getHistoricalData(ticker, date);
	}
	
}
