package com.wmsi.sgx.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.HttpConfig;
import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.financials.CompanyFinancial;
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
	
	Resource companyIds = new ClassPathResource("data/sgx_companies.txt");
	
	@Autowired
	IndexerService indexerService;
	@DataProvider
	public Object[][] tickers(){
		try{
			File f = companyIds.getFile();
			List<String> lines = FileUtils.readLines(f);
			
			// Toss header
			lines.remove(0);
			
			// Just use for tests for now.
			lines = lines.subList(0, 40);
			
			int c = lines.size();
			String[][] ids = new String[c][3];
			
			for(int i =0; i < c; i++){
				ids[i] = lines.get(i).split("\t");
			}
			
			return ids;
		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
	
	
	//@Test(dataProvider="tickers")
	public void testCreateIndex(String companyId, String ticker, String gvKey) throws IOException, URISyntaxException, IndexerServiceException, CapIQRequestException{

		String index = "20140310";
		indexerService.createIndex(index);

		CompanyInfo companyInfo = capIQService.getCompanyInfo(ticker, "03/10/2014");
		indexerService.save("company", ticker, companyInfo, index);
		/*
		CompanyFinancial companyFinancial = capIQService.getCompanyFinancials(ticker, "LTM" );
		String id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, index);
		
		companyFinancial = capIQService.getCompanyFinancials(ticker, "FY" );
		id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, index);
		
		companyFinancial = capIQService.getCompanyFinancials(ticker, "FY-1" );
		id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, index);

		companyFinancial = capIQService.getCompanyFinancials(ticker, "FY-2" );
		id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, index);

		companyFinancial = capIQService.getCompanyFinancials(ticker, "FY-3" );
		id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, index);

		companyFinancial = capIQService.getCompanyFinancials(ticker, "FY-4" );
		id = companyFinancial.getTickerCode().concat(companyFinancial.getPeriod());
		indexerService.save("financial", id, companyFinancial, index);
*/
		List<List<HistoricalValue>> historicalData = capIQService.getHistoricalData(ticker, "03/10/2014");
		List<HistoricalValue> price = historicalData.get(0);
		
		for(HistoricalValue data : price){
			String id = data.getTickerCode().concat(Long.valueOf(data.getDate().getTime()).toString());
			indexerService.save("price", id, data, index);
		}

		List<HistoricalValue> volume = historicalData.get(1);
		
		for(HistoricalValue data : volume){
			String id = data.getTickerCode().concat(Long.valueOf(data.getDate().getTime()).toString());
			indexerService.save("volume", id, data, index);
		}		
		
	}
	
}
