package com.wmsi.sgx.service.indexer;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.Financial;
import com.wmsi.sgx.model.financials.Financials;
import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorIndexerService;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;
import com.wmsi.sgx.service.sandp.capiq.CapIQServiceException;
import com.wmsi.sgx.service.sandp.capiq.InvalidIdentifierException;

@Service
public class IndexBuilderServiceImpl implements IndexBuilderService{

	private static final Logger log = LoggerFactory.getLogger(IndexBuilderServiceImpl.class);
	
	@Autowired
	private CapIQService capIQService;

	public void setCapIQService(CapIQService capIQService) {
		this.capIQService = capIQService;
	}

	@Autowired
	private AlphaFactorIndexerService alphaFactorService;

	private Resource companyIds = new ClassPathResource("data/sgx_companies_short.txt");

	@Autowired
	private IndexerService indexerService;

	@Override
	public List<CompanyInputRecord> getTickers(@Header String indexName) throws IndexerServiceException {

		CSVReader csvReader = null;
		InputStreamReader reader = null;
		
		try{
			reader = new InputStreamReader(companyIds.getInputStream());
			csvReader = new CSVReader(reader, '\t');
			csvReader.readNext(); // skip header

			String[] record = null;

			List<CompanyInputRecord> ret = new ArrayList<CompanyInputRecord>();
			
			while((record = csvReader.readNext()) != null){
				
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
			IOUtils.closeQuietly(csvReader);
			IOUtils.closeQuietly(reader);
		}
	}

	@Override
	public CompanyInputRecord index(@Header String indexName, @Header Date jobDate, @Payload CompanyInputRecord input) throws IOException, IndexerServiceException, CapIQRequestException, ParseException, CapIQServiceException{
		
		SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
		String date = fmt.format(jobDate);

		
		try{
			index(indexName, date, input.getTicker());
		}
		catch(InvalidIdentifierException e){
			log.error("Invalid id " + input.getTicker());
		}

		input.setIndexed(true);
		return input;
	}
	
	public boolean aggregation(Message msg){
			System.out.println(msg.getHeaders());
			return true;
	}

	@Override
	public Boolean buildAlphaFactors(@Header String indexName) throws AlphaFactorServiceException, IndexerServiceException{
		File file = alphaFactorService.getLatestFile();
		List<AlphaFactor> factors = alphaFactorService.loadAlphaFactors(file);

		for(AlphaFactor f : factors){
			indexerService.save("alphaFactor", f.getId(), f, indexName);
		}
		
		return true;
	}
	
	
	private void index(String index, String date, String ticker) throws IOException,
			IndexerServiceException, CapIQRequestException, ParseException, InvalidIdentifierException, CapIQServiceException {

		Company companyInfo = capIQService.getCompanyInfo(ticker, date);

		if(companyInfo == null)
			return;
		
		indexerService.save("company", ticker, companyInfo, index);

		Holders h = capIQService.getHolderDetails(ticker);

		if(h != null)
			indexerService.save("holders", ticker, h, index);

		KeyDevs kd = capIQService.getKeyDevelopments(ticker, date);

		if(kd != null)
			indexerService.save("keyDevs", ticker, kd, index);

		String currency = companyInfo.getFilingCurrency();
		
		if(StringUtils.isEmpty(currency))
			currency = "SGD";
		
		Financials financials = capIQService.getCompanyFinancials(ticker, currency);

		for(Financial c : financials.getFinancials()){
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