package com.wmsi.sgx.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.indexer.IndexerService;
import com.wmsi.sgx.service.indexer.IndexerServiceException;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorIndexerService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;

@RestController
@RequestMapping(value="doBiu", produces="application/json")
public class IndexBuilderController{

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
	
	
	@RequestMapping(method=RequestMethod.POST)
	public String buildIndex(@RequestBody Map<String, String> bod ) throws IOException, URISyntaxException, IndexerServiceException, CapIQRequestException, ParseException{
		// TEMP TEMP TEMP - Quick and sleezy way to build the index
		// while deployed until a nightly build is implemented
		System.out.println(bod);
		if(!bod.get("pass").equals("Jon1 m1Tch3ll N3v3R L13d"))
			return "invalid";
		
		String index = bod.get("name");
		String date = bod.get("date");
		String size = bod.get("size");
		indexerService.createIndex(index);

		for(String[] ticker : tickers(size)){
			if(ticker[1] == null || ticker[1].toLowerCase().equals("null"))
				continue;
			
			index(index, date, ticker[0], ticker[1], ticker[2]);
		}
			
		buildAlphaFactors(index, date);
		return "ok";
	}

	private void buildAlphaFactors(String index, String date) throws IOException{
		Resource r = new ClassPathResource("data/rank_AFLSG_20140311.txt");
		List<AlphaFactor> factors = alphaFactorService.loadAlphaFactors(r.getFile());
	
		for(AlphaFactor f : factors){
			indexerService.save("alphaFactor", f.getId(), f, index);
		}
	}
	
	private void index(String index, String date, String companyId, String ticker, String gvKey) throws IOException, URISyntaxException, IndexerServiceException, CapIQRequestException, ParseException{

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

		List<List<HistoricalValue>> historicalData = capIQService.getHistoricalData(ticker, date);
		List<HistoricalValue> price = historicalData.get(0);
		
		for(HistoricalValue data : price){
			id = data.getTickerCode().concat(Long.valueOf(data.getDate().getTime()).toString());
			indexerService.save("price", id, data, index);
		}

		List<HistoricalValue> volume = historicalData.get(1);
		
		for(HistoricalValue data : volume){
			id = data.getTickerCode().concat(Long.valueOf(data.getDate().getTime()).toString());
			indexerService.save("volume", id, data, index);
		}		
		
	}
}