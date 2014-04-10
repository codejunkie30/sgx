package com.wmsi.sgx.service.indexer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorIndexerService;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;

@Service
public class IndexBuilderServiceImpl implements IndexBuilderService{

	@Autowired
	private CapIQService capIQService;
	
	@Autowired
	private AlphaFactorIndexerService alphaFactorService;
	
	private Resource companyIds = new ClassPathResource("data/sgx_companies.txt");
	
	@Autowired
	private IndexerService indexerService;
	
	public String[][] tickers(String size){
		try{
			File f = companyIds.getFile();
			List<String> lines = FileUtils.readLines(f);
			
			// Toss header
			lines.remove(0);
			
			// Just use for tests for now.
			if(size != null)
			lines = lines.subList(0, Integer.valueOf(size));
			
			int c = lines.size();
			String[][] ids = new String[c][2];
			
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
	
	
	@Override
	public String buildIndex() throws IOException, URISyntaxException, IndexerServiceException, CapIQRequestException, ParseException, AlphaFactorServiceException{
		String index = "test";
		String date = "03/10/2014";
		String size = "15";
		indexerService.createIndex(index);

		for(String[] ticker : tickers(size)){
			if(ticker[1] == null || ticker[1].toLowerCase().equals("null"))
				continue;
			
			index(index, date, ticker[0], ticker[1]);
		}
			
		buildAlphaFactors(index, date);
		return "ok";
	}

	@Autowired 
	private DefaultFtpSessionFactory ftpSessionFactory;
	
	private void buildAlphaFactors(String index, String date) throws IOException, AlphaFactorServiceException{
		
		File file = alphaFactorService.getLatestFile();
		List<AlphaFactor> factors = alphaFactorService.loadAlphaFactors(file);
	
		for(AlphaFactor f : factors){
			indexerService.save("alphaFactor", f.getId(), f, index);
		}
	}
	
	private void index(String index, String date, String companyId, String ticker) throws IOException, URISyntaxException, IndexerServiceException, CapIQRequestException, ParseException{

		CompanyInfo companyInfo = capIQService.getCompanyInfo(ticker, date);
		if(companyInfo == null)
			return;
		indexerService.save("company", ticker, companyInfo, index);
		
		Holders h = capIQService.getHolderDetails(ticker);
		
		if(h != null)
			indexerService.save("holders", ticker, h, index);

		KeyDevs kd = capIQService.getKeyDevelopments(ticker, date);

		if(kd != null)
			indexerService.save("keyDevs", ticker, kd, index);
		
		//CompanyFinancial companyFinancial = capIQService.getCompanyFinancials(ticker, "LTM" );
		List<CompanyFinancial> cfs = capIQService.getCompanyFinancials(ticker);
		for(CompanyFinancial c : cfs){
			String id = c.getTickerCode().concat(c.getAbsPeriod());
			indexerService.save("financial", id, c, index);
		}
		
		
		/*
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
		List<List<HistoricalValue>> historicalData = capIQService.getHistoricalData(ticker, date);
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