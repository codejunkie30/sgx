package com.wmsi.sgx.service.indexer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.DividendValue;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.GovTransparencyIndex;
import com.wmsi.sgx.model.GovTransparencyIndexes;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.VolWeightedAvgPrice;
import com.wmsi.sgx.model.VolWeightedAvgPrices;
import com.wmsi.sgx.model.indexer.Index;
import com.wmsi.sgx.model.indexer.Indexes;
import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.service.gti.GtiService;
import com.wmsi.sgx.service.indexer.IndexBuilderService;
import com.wmsi.sgx.service.indexer.IndexerService;
import com.wmsi.sgx.service.indexer.IndexerServiceException;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorIndexerService;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;
import com.wmsi.sgx.service.sandp.capiq.InvalidIdentifierException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.service.vwap.VwapService;

@Service
public class IndexBuilderServiceImpl implements IndexBuilderService{
	private static final Logger log = LoggerFactory.getLogger(IndexBuilderServiceImpl.class);

	@Value("${elasticsearch.index.prefix}")
	private String indexPrefix;
	
	@Value("${elasticsearch.index.name}")
	private String indexAlias;

	@Value("${indexer.failureThreshold}")
	private int FAILURE_THRESHOLD;
	
	@Value("${loader.ticker.file}")
	private String tickerFile;
	
	@Value("${loader.fx.file}")
	private String fxFile = "/mnt/data/fx-conversion.csv";
	
	@Autowired
	private CapIQService capIQService;

	@Autowired
	private AlphaFactorIndexerService alphaFactorService;

	@Autowired
	private GtiService gtiService;

	@Autowired
	private IndexerService indexerService;
	
	@Autowired
	private VwapService vwapService;

	public void setCapIQService(CapIQService capIQService) {
		this.capIQService = capIQService;
	}
	
	@Override
	public List<CompanyInputRecord> readTickers(@Header String indexName, @Header Date jobDate) throws IndexerServiceException {

		log.info("Reading tickers from input file...");
		log.info("Tickers loaded from: {}", tickerFile);
		
		CSVReader csvReader = null;
		InputStreamReader reader = null;
		
		try{
			SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
			String date = fmt.format(jobDate);
			reader = new InputStreamReader(new FileInputStream(tickerFile));
			csvReader = new CSVReader(reader, ',');
			csvReader.readNext(); // skip header

			String[] record = null;			
			List<CompanyInputRecord> ret = new ArrayList<CompanyInputRecord>();
			
			while((record = csvReader.readNext()) != null){
				
				CompanyInputRecord r = new CompanyInputRecord();
				r.setId(record[6]);
				r.setLegalName(record[0]);
				r.setTicker(record[1]);
				r.setExchangeSymbol(record[2]);
				r.setIsin(record[3]);
				r.setTradeName(record[4].length() == 0 ? r.getLegalName() : record[4]);
				r.setCurrency(record[8]);
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
		
		long start = System.currentTimeMillis();
		
		try{
			log.debug("Indexing record: {}", input.getTicker());
			indexRecord(indexName, input);
		}
		catch(InvalidIdentifierException e){
			// Allow bad tickers to flow through ie. don't consider it an error
			log.error("Invalid id " + input.getTicker());
		}
		catch(Exception ex) {
			log.error("Invalid record: " + input.getTicker(), ex);
		}
		
		input.setIndexed(true);
		
		log.debug("Record {} took {} ms", input.getTicker(), (System.currentTimeMillis()-start));
		
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
		
		List<CompanyInputRecord> failedRecords = new ArrayList<CompanyInputRecord>();
		
		for(CompanyInputRecord rec : records){
			if(!rec.getIndexed())
				failedRecords.add(rec);				
		}
		
		int failed = failedRecords.size();
		boolean success = failed < FAILURE_THRESHOLD;
		
		log.info("Job status completed with {} failed records. Success: {}", failed, success);
		
		if(log.isDebugEnabled()){
			
			if(failed > 0)
				log.debug("Failed records:\n{}", StringUtils.collectionToDelimitedString(failedRecords, "\n"));
			
		}
		
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
		
		if(company == null) return;
		
		company.setFilingCurrency(input.getCurrency());
		company.setTradeName(input.getTradeName());
		
		PriceHistory historicalData = company.fullPH;
		company.fullPH = null; // HACK to keep from serializing
		
		String tickerNoExchange = company.getTickerCode();

		loadCompanyGTI(company);
		loadCompanyVWAP(company);
		indexerService.save("company", tickerNoExchange, company, index);
		
		GovTransparencyIndexes gtis = gtiService.getForTicker(tickerNoExchange);
		if(gtis != null) indexerService.save("gtis", tickerNoExchange, gtis, index);

		Holders h = capIQService.getHolderDetails(input);
		if(h != null) indexerService.save("holders", tickerNoExchange, h, index);
		
		KeyDevs kd = capIQService.getKeyDevelopments(input);
		if(kd != null) indexerService.save("keyDevs", tickerNoExchange, kd, index);
		
		DividendHistory dH = capIQService.getDividendData(input);
		if(dH != null) indexerService.save("dividendHistory", tickerNoExchange, dH, index);

		String currency = company.getFilingCurrency();
		if(StringUtils.isEmpty(currency)) currency = "SGD";
		Financials financials = capIQService.getCompanyFinancials(input, currency);
		
		for(Financial c : financials.getFinancials()){
			String id = c.getTickerCode().concat(c.getAbsPeriod());
			c.setFilingCurrency(company.getFilingCurrency());
			indexerService.save("financial", id, c, index);
		}
		
		if (historicalData != null) {
			List<HistoricalValue> hvs = historicalData.getPrice();
			saveHistorical("price", hvs, tickerNoExchange, index);
			
			hvs = historicalData.getHighPrice();
			saveHistorical("highPrice", hvs, tickerNoExchange, index);

			hvs = historicalData.getLowPrice();
			saveHistorical("lowPrice", hvs, tickerNoExchange, index);

			hvs = historicalData.getOpenPrice();
			saveHistorical("openPrice", hvs, tickerNoExchange, index);

			hvs = historicalData.getVolume();
			saveHistorical("volume", hvs, tickerNoExchange, index);
		}

		DividendHistory dividendData = capIQService.getDividendData(input);
		List<DividendValue> dividendValue = dividendData.getDividendValues();
		if (dividendValue != null) {
			for(DividendValue data : dividendValue){
				String id = tickerNoExchange.concat(Long.valueOf(data.getDividendExDate().getTime()).toString());
				indexerService.save("dividendValue", id, data, index);
			}
		}

		Estimates estimates = capIQService.getEstimates(input);
		if (estimates != null) {
			for(Estimate e : estimates.getEstimates()){
				String id = e.getTickerCode().concat(e.getPeriod());
				indexerService.save("estimate", id, e, index);
			}
		}

		
	}
	
	private void saveHistorical(String type, List<HistoricalValue> values, String ticker, String index) throws IndexerServiceException {
		StringBuilder buffer = new StringBuilder(); 
		for(HistoricalValue data : values){
			String val = Long.valueOf(data.getDate().getTime()).toString();
			buffer.append("{ \"index\": { \"_id\": \"" + ticker.concat(val)  + "\" }}\n");
			buffer.append("{ \"tickerCode\": \"" + ticker + "\", \"value\": " + data.getValue() + ", \"date\": " + val + "}\n");
		}
		if (buffer.length() > 0) indexerService.bulkSave(type, buffer.toString(), index);
		buffer.setLength(0);
	}
	
	private void loadCompanyVWAP(Company company){
		
		BigDecimal value = BigDecimal.ZERO;
		BigDecimal volume = BigDecimal.ZERO;
		Double vwapValue = null;
		
		VolWeightedAvgPrices vwap = vwapService.getForTicker(company.getTickerCode());
		List<VolWeightedAvgPrice> indexes = vwap.getVwaps();
		Date date = null;
		String currency = null;
		/*
		Boolean sixMonths = false;
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -6);
		Date sixMonthsAgo = cal.getTime();
		*/
		if(indexes.size() > 0){			
			//Date earliestDate = indexes.get(indexes.size() - 1).getDate();
			//sixMonths = earliestDate.before(sixMonthsAgo);
			
			
			date = indexes.get(0).getDate();
			currency = indexes.get(0).getCurrency();
			
			
		}
		
		
		for(int i=0; i < indexes.size(); i++){		
			
			String val = indexes.get(i).getValue();
			String vol = indexes.get(i).getVolume(); 			
			
			value = value.add(new BigDecimal(val));			
			volume = volume.add(new BigDecimal(vol));
			
		}
		if(volume.compareTo(BigDecimal.ZERO) != 0)
			vwapValue = value.divide(volume, 6,RoundingMode.HALF_UP).doubleValue();
		
	
		
		company.setVolWeightedAvgPrice(vwapValue);
		company.setVwapAsOfDate(date);
		company.setVwapCurrency(currency);
		
	}

	private void loadCompanyGTI(Company company){
		GovTransparencyIndex gti = gtiService.getLatest(company.getTickerCode());
		
		if(gti == null)
			return;
		
		company.setGtiScore(gti.getTotalScore());
		company.setGtiRankChange(gti.getRankChange());
	}
	
	@Override
	public Boolean createFXIndex(@Header String indexName, @Header int fxBatchSize) throws IndexerServiceException {

		log.info("Creating FX index");
		
		String json = "{ \"index\": { }}\n";
		json += "{ \"from\": \"%s\", \"to\": \"%s\", \"date\": \"%4s-%2s-%2s\", \"multiplier\": %s }\n";
		
		StringBuilder buffer = new StringBuilder(); int cnt = 0;
		try(BufferedReader br = new BufferedReader(new FileReader(fxFile))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	Object[] vals = parseFXLine(line); 
		    	if (vals == null) continue;
		    	buffer.append(String.format(json, vals).replace("- ", "-0"));
		    	if (cnt % fxBatchSize == 0 && cnt > 0) {
		    		log.info("FX Processed {} records", cnt);
		    		indexerService.bulkSave("fxdata", buffer.toString(), indexName);
		    		buffer.setLength(0);
		    	}
		    	cnt++;
		    }
		    if (buffer.length() > 0) indexerService.bulkSave("fxdata", buffer.toString(), indexName);
		    buffer.setLength(0);
		}
		catch(Exception e) {
			throw new IndexerServiceException("Trying to create FX conversion index", e);
		}
		
		log.info("Finished Creating FX index with {} records", cnt);
		
		return false;
	}
	
	public Object[] parseFXLine(String line) {
		try {
			String[] vals = line.replaceAll("\"", "").split(",");
			if (!vals[1].equals("SGD")) return null; // temp hack to speed things up, only conversions to SGD
			String[] dt = vals[2].split(" ")[0].split("/");
			return new Object[] { vals[0], vals[1], dt[2], dt[0], dt[1], vals[3] };
		}
		catch(Exception e) {}
		return null;
	}

	
	
}