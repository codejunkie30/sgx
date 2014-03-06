package com.wmsi.sgx.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

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
import com.wmsi.sgx.model.CompanyFinancial;
import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.service.indexer.IndexerService;
import com.wmsi.sgx.service.indexer.IndexerServiceException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class IndexerServiceTest extends AbstractTestNGSpringContextTests{

	@Configuration
	@ComponentScan(basePackageClasses = {IndexerServiceTest.class})
	@Import(HttpConfig.class)
	static class IndexerServiceTestConfig{}
	

	@Autowired
	CapIQService capIQService;
	
	@Autowired
	IndexerService indexerService;
	@DataProvider
	public Object[][] tickers(){
	return new Object[][]{
	{"A7S"},
	{"A55"},
	{"H1Q"},
	{"H1M"},
	{"ES3" },
	{"S45U"},
	{"NULL"},
	{"A35"},
	{"G3B"},
	{"OV8"},
	{"ON7"},
	{"O2I"},
	{"Q5T"},
	{"Y35"},
	{"RC5"},
	{"RE2"},
	{"RE4"},
	{"SK3"},
	{"SH8"},
	{"SK6U"},
	{"DT2"},
	{"DU4"},
	{"C9Q"},
	{"DC9"},
	{"D4N"},
	{"D1R"},
	{"T8JU"},
	{"5LY"},
	{"D2U"},
	{"EB5"},
	{"D2V"}
	};
	}

	@Test(dataProvider="tickers")
	public void testCreateIndex(String ticker) throws IOException, URISyntaxException, IndexerServiceException, CapIQRequestException{
		indexerService.createIndex("20140304");

		CompanyInfo companyInfo = capIQService.getCompanyInfo(ticker);
		indexerService.save("company", ticker, companyInfo, "20140304");
		
		CompanyFinancial companyFinancial = capIQService.getCompanyFinancials(ticker, "LTM" );
		String id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, "20140304");
		
		companyFinancial = capIQService.getCompanyFinancials(ticker, "FY" );
		id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, "20140304");
		
		companyFinancial = capIQService.getCompanyFinancials(ticker, "FY-1" );
		id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, "20140304");

		companyFinancial = capIQService.getCompanyFinancials(ticker, "FY-2" );
		id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, "20140304");

		companyFinancial = capIQService.getCompanyFinancials(ticker, "FY-3" );
		id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, "20140304");

		companyFinancial = capIQService.getCompanyFinancials(ticker, "FY-4" );
		id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, "20140304");

		List<List<HistoricalValue>> historicalData = capIQService.getHistoricalData(ticker, "02/28/2014");
		List<HistoricalValue> price = historicalData.get(0);
		
		for(HistoricalValue data : price){
			id = data.getTickerCode().concat(Long.valueOf(data.getDate().getTime()).toString());
			indexerService.save("historical", id, data, "20140304");
		}

		List<HistoricalValue> volume = historicalData.get(1);
		
		for(HistoricalValue data : volume){
			id = data.getTickerCode().concat(Long.valueOf(data.getDate().getTime()).toString());
			indexerService.save("volume", id, data, "20140304");
		}
		
	}
	
}
