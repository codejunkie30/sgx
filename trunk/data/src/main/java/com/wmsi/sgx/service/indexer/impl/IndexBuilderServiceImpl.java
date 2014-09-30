package com.wmsi.sgx.service.indexer.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.indexer.Index;
import com.wmsi.sgx.model.indexer.Indexes;
import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.service.indexer.IndexBuilderService;
import com.wmsi.sgx.service.indexer.IndexerService;
import com.wmsi.sgx.service.indexer.IndexerServiceException;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorIndexerService;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;
import com.wmsi.sgx.service.sandp.capiq.InvalidIdentifierException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@Service
public class IndexBuilderServiceImpl implements IndexBuilderService{

	private static final Logger log = LoggerFactory.getLogger(IndexBuilderServiceImpl.class);

	@Value("${elasticsearch.index.prefix}")
	private String indexPrefix;
	
	@Value("${elasticsearch.index.name}")
	private String indexAlias;

	@Value("${indexer.failureThreshold}")
	private int FAILURE_THRESHOLD;
	
	@Autowired
	private CapIQService capIQService;

	@Autowired
	private AlphaFactorIndexerService alphaFactorService;

	@Autowired
	private IndexerService indexerService;

	public void setCapIQService(CapIQService capIQService) {
		this.capIQService = capIQService;
	}

	@Override
	public List<CompanyInputRecord> readTickers(@Header String indexName, @Header Date jobDate, Resource tickers) throws IndexerServiceException {

		log.info("Reading tickers from input file...");
		log.info("Tickers loaded from: {}", tickers.getDescription());
		
		CSVReader csvReader = null;
		InputStreamReader reader = null;
		
		try{
			SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
			String date = fmt.format(jobDate);

			reader = new InputStreamReader(tickers.getInputStream());
			csvReader = new CSVReader(reader, ',');
			csvReader.readNext(); // skip header

			String[] record = null;			
			List<CompanyInputRecord> ret = new ArrayList<CompanyInputRecord>();
			
			while((record = csvReader.readNext()) != null){
				
				CompanyInputRecord r = new CompanyInputRecord();
				r.setId(record[0].trim());
				
				String ticker = record[1].trim();
				
				if(record[1].indexOf(' ') > 0){
					// Remove junk from end of ticker string
					ticker = record[1].trim().split(" ")[0];
				}
				
				r.setTicker(ticker);
				
				r.setTradeName(record[3].trim());
				r.setDate(date);
				ret.add(r);
			}
		
			log.info("Found {} tickers to process", ret.size());
			
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
	public CompanyInputRecord index(@Header String indexName, @Payload CompanyInputRecord input) throws IndexerServiceException, CapIQRequestException, ResponseParserException{
		
		try{
			indexRecord(indexName, input);
		}
		catch(InvalidIdentifierException e){
			// Allow bad tickers to flow through ie. don't consider it an error
			log.error("Invalid id " + input.getTicker());
		}

		input.setIndexed(true);
		return input;
	}

	@Override
	public Boolean buildAlphaFactors(@Header String indexName) throws AlphaFactorServiceException, IndexerServiceException{
		
		log.info("Building alpha factors");
		
		File file = alphaFactorService.getLatestFile();
		List<AlphaFactor> factors = alphaFactorService.loadAlphaFactors(file);

		for(AlphaFactor f : factors){
			indexerService.save("alphaFactor", f.getId(), f, indexName);
		}
		
		log.info("Completed building of alpha factors");
		
		return true;
	}
	
	/**
	 * Determine if the index job succeeded by checking the number of 
	 * records that failed to index against a pre-determined threshold. 
	 */
	@Override
	public Boolean isJobSuccessful(@Payload List<CompanyInputRecord> records){
		
		log.info("Checking job successful with failure threshold: {}", FAILURE_THRESHOLD);
		
		int failed = 0;
		
		for(CompanyInputRecord rec : records){
			if(!rec.getIndexed())
				failed++;
		}
		
		boolean success = failed < FAILURE_THRESHOLD;
		
		log.info("Job status completed with {} failed records. Success: {}", failed, success);
		
		return success;
	}
	
	private static final int INDEX_REMOVAL_THRESHOLD = 5;
	
	@Override
	public void deleteOldIndexes() throws IndexerServiceException{
		
		log.info("Removing indexes greater than {} days old.", INDEX_REMOVAL_THRESHOLD);
		
		Indexes indexes = indexerService.getIndexes();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, INDEX_REMOVAL_THRESHOLD * -1);
		Date fiveDaysAgo = cal.getTime();
		
		int removed = 0;
		
		for(Index index : indexes.getIndexes()){
			String indexName = index.getName();
			String date = index.getName().substring(indexPrefix.length(), indexName.length());

			Date indexDate = new Date(Long.parseLong(date));
			int dif = fiveDaysAgo.compareTo(indexDate);
			
			if(dif > 0){
				// Make sure we don't delete the live index, even if it's older than the threshold
				if(index.getAliases() != null && index.getAliases().contains(indexAlias)){
					log.warn("Found alias on index older than {} days. Skipping deletion of this index {}", INDEX_REMOVAL_THRESHOLD, indexName);
					continue;
				}

				log.info("Deleting index {}", indexName);
				
				indexerService.deleteIndex(indexName);
				removed++;
			}
		}

		log.info("Index cleanup complete. Removed {} old indexes", removed);
	}

	
	private void indexRecord(String index, CompanyInputRecord input) throws IndexerServiceException, CapIQRequestException, ResponseParserException {

		Company company = capIQService.getCompany(input);
		
		if(company == null)
			return;
		
		String tickerNoExchange = company.getTickerCode();
		
		if(tickerNoExchange == null){
			// This seems to happen if a ticker if valid but the company merged. 
			log.warn("Warning: CapIQService returned company with null ticker"); 
			throw new InvalidIdentifierException("Ticker not found " + input.getTicker());
		}
		
		indexerService.save("company", tickerNoExchange, company, index);

		Holders h = capIQService.getHolderDetails(input);

		if(h != null)
			indexerService.save("holders", tickerNoExchange, h, index);

		KeyDevs kd = capIQService.getKeyDevelopments(input);

		if(kd != null)
			indexerService.save("keyDevs", tickerNoExchange, kd, index);

		String currency = company.getFilingCurrency();
		
		if(StringUtils.isEmpty(currency))
			currency = "SGD";
		
		Financials financials = capIQService.getCompanyFinancials(input, currency);

		for(Financial c : financials.getFinancials()){
			String id = c.getTickerCode().concat(c.getAbsPeriod());
			indexerService.save("financial", id, c, index);
		}

		PriceHistory historicalData = capIQService.getHistoricalData(input);
		
		List<HistoricalValue> price = historicalData.getPrice();

		for(HistoricalValue data : price){
			String id = tickerNoExchange.concat(Long.valueOf(data.getDate().getTime()).toString());
			indexerService.save("price", id, data, index);
		}

		List<HistoricalValue> volume = historicalData.getVolume();

		for(HistoricalValue data : volume){
			String id = tickerNoExchange.concat(Long.valueOf(data.getDate().getTime()).toString());
			indexerService.save("volume", id, data, index);
		}

	}

}