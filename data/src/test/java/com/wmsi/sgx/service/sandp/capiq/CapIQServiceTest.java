package com.wmsi.sgx.service.sandp.capiq;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.HttpConfig;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.model.integration.CompanyInputRecordBuilder;

@ContextConfiguration(classes={HttpConfig.class})
public class CapIQServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	public CapIQService capIQService;
	
	@DataProvider
	Object[][] testTickers(){
		String date = "07/31/2014";
		
		return new Object[][]{
				{"C6L", date}, 
				{"A7S", date}
		};
	}
	
	//@Test(groups = {"integration"},dataProvider="testTickers")
	public void testGetCompanyInfo(String ticker, String date) throws CapIQRequestException, ResponseParserException{
		CompanyInputRecord rec = CompanyInputRecordBuilder
				.companyInputRecord()
				.withDate(date)
				.withTicker(ticker)
				.build();
		
		capIQService.getCompany(rec);
	}
	
	//@Test(groups = {"integration"},dataProvider="testTickers")
	public void testGetCompanyFinancials(String ticker, String date) throws CapIQRequestException, ResponseParserException{
		CompanyInputRecord rec = CompanyInputRecordBuilder
				.companyInputRecord()
				.withDate(date)
				.withTicker(ticker)
				.build();

		capIQService.getCompanyFinancials(rec, "SGD");
	}
	
	//@Test(groups = {"integration"},dataProvider="testTickers")
	public void testGetHistoricalData(String ticker, String date) throws CapIQRequestException, ResponseParserException{
		CompanyInputRecord rec = CompanyInputRecordBuilder
				.companyInputRecord()
				.withDate(date)
				.withTicker(ticker)
				.build();

		capIQService.getHistoricalData(rec);
	}

	//@Test(groups = {"integration"},dataProvider="testTickers")
	public void testGetKeyDevs(String ticker, String date) throws CapIQRequestException, ResponseParserException{
		CompanyInputRecord rec = CompanyInputRecordBuilder
				.companyInputRecord()
				.withDate(date)
				.withTicker(ticker)
				.build();

	/*	KeyDevs keyDevs = capIQService.getKeyDevelopments(rec);
		assertNotNull(keyDevs);		
		assertNotNull(keyDevs.getKeyDevs());		*/
	}
	
}
