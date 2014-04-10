package com.wmsi.sgx.service.indexer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Payload;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.integration.CompanyInputRecord;
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

	private Resource companyIds = new ClassPathResource("data/sgx_companies_short.txt");

	@Autowired
	private IndexerService indexerService;

	@Override
	public List<CompanyInputRecord> getTickers() throws IndexerServiceException {

		CSVReader reader = null;

		try{
			File f = companyIds.getFile();

			List<CompanyInputRecord> ret = new ArrayList<CompanyInputRecord>();

			reader = new CSVReader(new FileReader(f), '\t');
			reader.readNext(); // skip header

			String[] record = null;

			while((record = reader.readNext()) != null){
				
				CompanyInputRecord r = new CompanyInputRecord();
				r.setId(record[0]);
				r.setTicker(record[1]);				
				
				ret.add(r);
			}
			
			return ret;
		}
		catch(IOException e){
			throw new IndexerServiceException("Error parsing ticker input file", e);
		}
		finally{
			IOUtils.closeQuietly(reader);
		}
	}

	@Override
	public void index(@Payload CompanyInputRecord input) throws IndexerServiceException {
		String index = "test";
		String date = "03/10/2014";
		
		try{
			indexerService.createIndex(index);
			index(index, date, input.getTicker());
		}
		catch(IOException | URISyntaxException | IndexerServiceException | CapIQRequestException | ParseException e){
			throw new IndexerServiceException("Failed to index ticker " + input.getTicker(), e);
		}		
	}


	@Autowired
	private DefaultFtpSessionFactory ftpSessionFactory;

	@Override
	public void buildAlphaFactors(@Payload CompanyInputRecord input) throws IOException, AlphaFactorServiceException {
		String index = "test";
		
		File file = alphaFactorService.getLatestFile();
		List<AlphaFactor> factors = alphaFactorService.loadAlphaFactors(file);

		for(AlphaFactor f : factors){
			indexerService.save("alphaFactor", f.getId(), f, index);
		}
	}

	private void index(String index, String date, String ticker) throws IOException,
			URISyntaxException, IndexerServiceException, CapIQRequestException, ParseException {

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

		List<CompanyFinancial> cfs = capIQService.getCompanyFinancials(ticker);

		for(CompanyFinancial c : cfs){
			String id = c.getTickerCode().concat(c.getAbsPeriod());
			indexerService.save("financial", id, c, index);
		}

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