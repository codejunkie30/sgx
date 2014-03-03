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
		
		return new Object[][]{
				{"C6L"}, 
				{"A7S"}
		};
	}
	
	@Test(dataProvider="testTickers")
	public void testGetCompanyInfo(String ticker) throws CapIQRequestException{
		capIQService.getCompanyInfo(ticker);
	}
}
