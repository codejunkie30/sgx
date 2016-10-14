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
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.wmsi.sgx.exception.ErrorBeanHelper;
import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.CurrencyModel;
import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.DividendValue;
import com.wmsi.sgx.model.ErrorBean;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.FXRecord;
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
import com.wmsi.sgx.service.currency.CurrencyService;
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
import com.wmsi.sgx.util.ElasticSearchIndexDateComparator;

import au.com.bytecode.opencsv.CSVReader;

@Service
public class IndexBuilderServiceImpl implements IndexBuilderService {
	private static final Logger log = LoggerFactory.getLogger(IndexBuilderServiceImpl.class);

	@Value("${elasticsearch.index.prefix}")
	private String indexPrefix;

	@Value("${elasticsearch.index.name}")
	private String indexAlias;

	@Value("${elasticsearch.index.liveIndexes}")
	private String liveIndexes;

	@Value("${indexer.failureThreshold}")
	private int FAILURE_THRESHOLD;

	@Value("${loader.ticker.file}")
	private String tickerFile;

	@Value("${loader.fx.file}")
	private String fxFile;
	//private String fxFile = "/mnt/data/fx-conversion.csv";

	@Autowired
	private CapIQService capIQService;

	@Autowired
	private AlphaFactorIndexerService alphaFactorService;

	@Autowired
	private GtiService gtiService;

	@Autowired
	private IndexerService indexerService;
	
	@Autowired
	private CurrencyService currencyService;
	
	@Autowired
	private ErrorBeanHelper errorBeanHelper;
	
	private LinkedList<String> currencyList = new LinkedList<String>();

	@Autowired
	private VwapService vwapService;
	
	@Autowired
	private com.wmsi.sgx.util.EmailService emailService;
	
	@Value ("${email.dataload.complete}")
	public String toSite;

	@Value("${loader.currencies.file}")
	public String currenciesFile;
	
	@Value("${loader.workdir}")
	private String tmpDir;
	
	/**
	 * Saves List of available currencies from CSV files 
	 *            
	 * @return boolean
	 */
	public boolean saveCurrencyList()throws IndexerServiceException{
		String[] record = null;
		CSVReader csvReader = null;
		InputStreamReader reader = null;
		List<CurrencyModel>currencyModelList = new ArrayList<CurrencyModel>();
		try {
			reader = new InputStreamReader(new FileInputStream(currenciesFile));
			csvReader = new CSVReader(reader, ',');
			csvReader.readNext();
			while ((record = csvReader.readNext()) != null) {
				CurrencyModel model =new CurrencyModel();
				model.setCompleted(false);
				model.setCurrencyName(record[0].toLowerCase() + "_premium");
				model.setDescription(record[1]);
				currencyModelList.add(model);
			}
			//check for updates
			return checkForCurrencyChanges(currencyModelList);
			//delete existing records
			//currencyService.deleteAll();
			//return currencyService.addCurrencies(currencyModelList);

		} catch (IOException e) {
			errorBeanHelper.addError(new ErrorBean("IndexBuilderServiceImpl:saveCurrencyList",
					"Error saving currency list ", ErrorBean.ERROR, errorBeanHelper.getStackTrace(e)));
			throw new IndexerServiceException("Error saving currency list ", e);
		} finally {
			IOUtils.closeQuietly(csvReader);
			IOUtils.closeQuietly(reader);
		}
		
	}
	
	/**
	 * Checks for new currencies 
	 * 
	 * @param List of currencyModels
	 * @return boolean
	 */
	private boolean checkForCurrencyChanges(List<CurrencyModel> currencyModelList) {
		boolean flag=true;
		//check if new currency added or deleted
		List<CurrencyModel> oldList = currencyService.getAllCurrencies();
		List<CurrencyModel> newCurrencyList = new ArrayList<CurrencyModel>();
		newCurrencyList.addAll(currencyModelList);
		StringBuffer sb = new StringBuffer();
		boolean newFlag=newCurrencyList.removeAll(oldList);
		boolean oldFlag=oldList.removeAll(currencyModelList);
		if(!oldList.isEmpty()&&oldFlag){
			sb.append("\n Note :- Following Currencies are removed  :- "+oldList.toString());
			currencyService.deleteCurrenciesList(oldList);
		}
		if(!newCurrencyList.isEmpty()){
			sb.append(" \n Note :- Following currencies are indexed :- "+newCurrencyList.toString());
			flag=currencyService.addCurrencies(newCurrencyList);
		}
		//reset the completed flag
		currencyService.resetCompletedFlag();
		if(sb.length()>0){
			//send email
			try {
				emailService.send(toSite, " Currency updates ", sb.toString());
			} catch (MessagingException e) {
				// Don't stop the process ; continue with dataload
				e.printStackTrace();
			}
		}

		return flag;
	}

	
	public void setCapIQService(CapIQService capIQService) {
		this.capIQService = capIQService;
	}
	
	/**
	 * Reads all the tickets and created List of CompanyInputRecords from it 
	 * 
	 * @param Elastic Search indexName
	 * @param date of the index
	 * @return List of CompanyInputRecords
	 */
	@Override
	public List<CompanyInputRecord> readTickers(@Header String indexName, @Header Date jobDate)
			throws IndexerServiceException {

		log.info("Reading tickers from input file...");
		log.info("Tickers loaded from: {}", tickerFile);
		log.info("index name", indexName);
		CSVReader csvReader = null;
		InputStreamReader reader = null;

		try {
			SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
			String date = fmt.format(jobDate);
			reader = new InputStreamReader(new FileInputStream(tickerFile));
			csvReader = new CSVReader(reader, ',');
			csvReader.readNext(); // skip header

			String[] record = null;
			List<CompanyInputRecord> ret = new ArrayList<CompanyInputRecord>();

			while ((record = csvReader.readNext()) != null) {

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
		} catch (IOException e) {
			errorBeanHelper.addError(new ErrorBean("IndexBuilderServiceImpl:readTickers",
					"Error parsing ticker input file", ErrorBean.ERROR, errorBeanHelper.getStackTrace(e)));
			throw new IndexerServiceException("Error parsing ticker input file", e);
		} finally {
			IOUtils.closeQuietly(csvReader);
			IOUtils.closeQuietly(reader);
		}
	}
	
	/**
	 * Index individual CompanyInputRecord into ES Index 
	 * 
	 * @param Elastic Search indexName
	 * @param CompanyInputRecord
	 * @return CompanyInputRecord
	 */
	@Override
	public CompanyInputRecord index(@Header String indexName, @Payload CompanyInputRecord input)
			throws IndexerServiceException, CapIQRequestException, ResponseParserException {

		long start = System.currentTimeMillis();

		try {
			log.debug("Indexing record: {}, Index name: {}", input.getTicker(), indexName);
			indexRecord(indexName, input);
		} catch (InvalidIdentifierException e) {
			// Allow bad tickers to flow through ie. don't consider it an error
			errorBeanHelper.addError(new ErrorBean("IndexBuilderServiceImpl:index",
					"Invalid Id", ErrorBean.ERROR, e.getMessage()));
			log.error("Invalid id " + input.getTicker());
		} catch (Exception ex) {
			errorBeanHelper.addError(new ErrorBean("IndexBuilderServiceImpl:index",
					"Invalid record: " + input.getTicker(), ErrorBean.ERROR, errorBeanHelper.getStackTrace(ex)));
			log.error("Invalid record: " + input.getTicker(), ex);
		}

		input.setIndexed(true);

		log.debug("Record {} took {} ms", input.getTicker(), (System.currentTimeMillis() - start));

		return input;
	}
	
	/**
	 * Create AlphaFactos from AlphaFactor file provided by S&P 
	 * 
	 * @param Elastic Search indexName
	 * @return Boolean
	 * @throws IndexerServiceException
	 * @throws AlphaFactorServiceException
	 */
	@Override
	public Boolean buildAlphaFactors(@Header String indexName)
			throws AlphaFactorServiceException, IndexerServiceException{

		log.info("Building alpha factors");

		File file = null;
		file = alphaFactorService.getLatestFile();
		if(file==null)return false;
		List<AlphaFactor> factors = alphaFactorService.loadAlphaFactors(file);

		for (AlphaFactor f : factors) {
			indexerService.save("alphaFactor", f.getId(), f, indexName);
		}

		log.info("Completed building of alpha factors");

		return true;
	}

	/**
	 * Determine if the index job succeeded by checking the number of records
	 * that failed to index against a pre-determined threshold.
	 * 
	 * @param List of CompanyInputRecords
	 * @param indexName
	 * @return int
	 * @throws IndexerServiceException
	 */
	@Override
	public int isJobSuccessful(@Payload List<CompanyInputRecord> records, @Header String indexName)
			throws IndexerServiceException {

		log.info("Checking job successful with failure threshold: {}", FAILURE_THRESHOLD);

		List<CompanyInputRecord> failedRecords = new ArrayList<CompanyInputRecord>();

		for (CompanyInputRecord rec : records) {
			if (!rec.getIndexed())
				failedRecords.add(rec);
		}

		int failed = failedRecords.size();
		boolean success = failed < FAILURE_THRESHOLD;

		log.info("Job status completed with {} failed records. Success: {}", failed, success);

		if (log.isDebugEnabled()) {

			if (failed > 0)
				log.debug("Failed records:\n{}", StringUtils.collectionToDelimitedString(failedRecords, "\n"));

		}

		if (success) {
			indexerService.createIndexAlias(indexName);
			deleteOldIndexes();
			updateCurrencyCompletedFlag(indexName);
		}

		boolean flag = hasCurrenciesCompleted();
		//send email if error 
		if(!success||flag){
			errorBeanHelper.sendEmail();
		}
		/*
		 * return (((currencyList != null & !currencyList.isEmpty() && indexName
		 * != null) &&
		 * currencyList.getLast().equalsIgnoreCase(indexName.substring(0,
		 * indexName.lastIndexOf("_")))) || currencyList.isEmpty()) ? 0 :
		 * (Boolean.TRUE.equals(success) ? 1 : -1);
		 */
		if (flag) {
			return 0;
		} else {
			if (success) {
				return 1;
			} else {
				return -1;
			}
		}
	}
	
	/**
	 * Updates CurrencyCompletedFlag
	 * 
	 * @param Elastic Search indexName 
	 * @param CompanyInputRecord
	 */
	private void updateCurrencyCompletedFlag(String indexName) {
		CurrencyModel model = new CurrencyModel();
		model.setCompleted(true);
		model.setCurrencyName(indexName.substring(0, indexName.lastIndexOf("_")));
		currencyService.updateCurrency(model);
	}
	
	/**
	 * Flag to check if Currency data load is complete
	 * 
	 * @return boolean
	 */
	private boolean hasCurrenciesCompleted(){
		return currencyService.getCountOfCurrenciesToComplete()<=0;
	}

	private static final int INDEX_REMOVAL_THRESHOLD = 5;
	
	
	/**
	 * Delete old stored indices from ES based on a threshold INDEX_REMOVAL_THRESHOLD
	 * 
	 * @throws IndexerServiceException
	 */
	@Override
	public void deleteOldIndexes() throws IndexerServiceException {

		log.info("Removing indexes greater than {} days old.", INDEX_REMOVAL_THRESHOLD);

		Indexes indexes = indexerService.getIndexes();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, INDEX_REMOVAL_THRESHOLD * -1);
		Date fiveDaysAgo = cal.getTime();

		List<String> liveIndexesList = new ArrayList<String>();
		if (liveIndexes != "" && liveIndexes.length() > 0) {
			for (int i = 0; i < liveIndexes.split(",").length; i++) {
				liveIndexesList.add(liveIndexes.split(",")[i]);
			}
		}
		int removed = 0;

		for (Index index : indexes.getIndexes()) {
			String indexName = index.getName();
			String date = index.getName().substring(indexPrefix.length(), indexName.length());

			Date indexDate = new Date(Long.parseLong(date));
			int dif = fiveDaysAgo.compareTo(indexDate);

			if (dif > 0) {
				// Make sure we don't delete the live index, even if it's older
				// than the threshold
				if (index.getAliases() != null && !(Collections.disjoint(index.getAliases(), liveIndexesList))) {
					log.warn("Found alias on index older than {} days. Skipping deletion of this index {}",
							INDEX_REMOVAL_THRESHOLD, indexName);
					continue;
				}

				log.info("Deleting index {} had indexDate as {}", indexName, indexDate);

				indexerService.deleteIndex(indexName);
				removed++;
			}
		}

		log.info("Index cleanup complete. Removed {} old indexes", removed);
	}
	
	/**
	 * Generates previous day Index name based on the current day's index date
	 * 
	 * @param Elastic Search indexName 
	 * 
	 * @return previous day index name
	 * 
	 * @throws IndexerServiceException
	 */
	public String getPreviousDayIndexName(String idxName) throws IndexerServiceException {

		Indexes indexes = indexerService.getIndexes();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1 * -1);
		Date oneDayAgo = cal.getTime();

		List<String> oneDayOldIndexes = new ArrayList<String>();

		for (Index index : indexes.getIndexes()) {

			String indexName = index.getName();
			if (indexName.substring(0, 11).equals(idxName.substring(0, 11))) {

				String date = index.getName().substring(indexPrefix.length(), indexName.length());
				Date indexDate = new Date(Long.parseLong(date));

				if (oneDayAgo.equals(indexDate) || oneDayAgo.after(indexDate)) {
					oneDayOldIndexes.add(date);
				}
			}
		}
		Collections.sort(oneDayOldIndexes, new ElasticSearchIndexDateComparator());

		if (oneDayOldIndexes.size() == 0) {
			log.info("No previous index Found, Previous index is set to current index");
			return idxName;
			// throw new UnsupportedOperationException("No previous index
			// Found");
		}
		// String previousDayIndex = indexPrefix+oneDayOldIndexes.get(0);
		String previousDayIndex = idxName.substring(0, indexPrefix.length()) + oneDayOldIndexes.get(0);
		log.debug("previoud say index found: " + previousDayIndex);
		return previousDayIndex;
	}
	
	/**
	 * This method combines all the data collected from different data services and load it into Elastic Search for an individual record
	 * to complete data load for that record.
	 * 
	 * @param Elastic Search indexName
	 * @param CompanyInputRecord
	 * @throws IndexerServiceException
	 * @throws CapIQRequestException
	 * @throws ResponseParserException
	 */
	private void indexRecord(String index, CompanyInputRecord input)
			throws IndexerServiceException, CapIQRequestException, ResponseParserException {

		Company company = capIQService.getCompany(input);

		if (company == null)
			return;

		if (company.getFilingCurrency() == null)
			company.setFilingCurrency(input.getCurrency());
		company.setTradeName(input.getTradeName());

		PriceHistory historicalData = company.fullPH;
		company.fullPH = null; // HACK to keep from serializing

		String tickerNoExchange = company.getTickerCode();

		loadCompanyGTI(company);
		loadCompanyVWAP(company);

		// HACK - reset exchange on save (SGX requested)
		String curExchange = company.getExchange();
		company.setExchange(curExchange.toUpperCase().equals("CATALIST") ? "SGX" : curExchange);
		indexerService.save("company", tickerNoExchange, company, index);
		company.setExchange(curExchange);

		GovTransparencyIndexes gtis = gtiService.getForTicker(tickerNoExchange);
		if (gtis != null)
			indexerService.save("gtis", tickerNoExchange, gtis, index);

		Holders h = capIQService.getHolderDetails(input);
		if (h != null)
			indexerService.save("holders", tickerNoExchange, h, index);

		KeyDevs kd = capIQService.getKeyDevelopments(input);
		if (kd != null)
			indexerService.save("keyDevs", tickerNoExchange, kd, index);

		DividendHistory dH = capIQService.getDividendData(input);
		if (dH != null)
			indexerService.save("dividendHistory", tickerNoExchange, dH, index);

		String currency = company.getFilingCurrency();
		if (StringUtils.isEmpty(currency))
			currency = index.substring(0, 3).toUpperCase();
		Financials financials = capIQService.getCompanyFinancials(input, currency);

		for (Financial c : financials.getFinancials()) {
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
			for (DividendValue data : dividendValue) {
				String id = tickerNoExchange.concat(Long.valueOf(data.getDividendExDate().getTime()).toString());
				indexerService.save("dividendValue", id, data, index);
			}
		}

		Estimates estimates = capIQService.getEstimates(input);
		if (estimates != null) {
			for (Estimate e : estimates.getEstimates()) {
				String id = e.getTickerCode().concat(e.getPeriod());
				indexerService.save("estimate", id, e, index);
			}
		}

	}
	/**
	 * Saves Historical Data for each ticker
	 * 
	 * @param Elastic Search indexName
	 * @param List<HistoricalValue> 
	 * @param ticker
	 * 
	 * @return
	 */
	private void saveHistorical(String type, List<HistoricalValue> values, String ticker, String index)
			throws IndexerServiceException {
		StringBuilder buffer = new StringBuilder();
		for (HistoricalValue data : values) {
			String val = Long.valueOf(data.getDate().getTime()).toString();
			buffer.append("{ \"index\": { \"_id\": \"" + ticker.concat(val) + "\" }}\n");
			buffer.append("{ \"tickerCode\": \"" + ticker + "\", \"value\": " + data.getValue() + ", \"date\": " + val
					+ "}\n");
		}
		if (buffer.length() > 0)
			indexerService.bulkSave(type, buffer.toString(), index);
		buffer.setLength(0);
	}

	/**
	 * Loads VWAP data for each company ticker 
	 * 
	 * @param Company Object
	 * @return
	 */
	private void loadCompanyVWAP(Company company) {

		BigDecimal value = BigDecimal.ZERO;
		BigDecimal volume = BigDecimal.ZERO;
		BigDecimal adjustedVolume = BigDecimal.ZERO;
		Double vwapValue = null;
		Double adjustedVwapValue = null;

		VolWeightedAvgPrices vwap = vwapService.getForTicker(company.getTickerCode());
		List<VolWeightedAvgPrice> indexes = vwap.getVwaps();
		Date date = null;
		String currency = null;

		if (indexes.size() > 0) {
			date = indexes.get(0).getDate();
			currency = indexes.get(0).getCurrency();
		}
		for (int i = 0; i < indexes.size(); i++) {

			String val = indexes.get(i).getValue();
			String vol = indexes.get(i).getVolume();
			String adjustmentFactor = indexes.get(i).getAdjustmentFactorValue();

			value = value.add(new BigDecimal(val));
			volume = volume.add(new BigDecimal(vol));
			adjustedVolume = adjustedVolume.add(new BigDecimal(adjustmentFactor).multiply(new BigDecimal(vol)));

		}
		if (volume.compareTo(BigDecimal.ZERO) != 0){
			vwapValue = value.divide(volume, 6, RoundingMode.HALF_UP).doubleValue();			
		}
		if(adjustedVolume.compareTo(BigDecimal.ZERO) != 0){
			adjustedVwapValue = value.divide(adjustedVolume, 6, RoundingMode.HALF_UP).doubleValue();
		}
		company.setAdjustedVolWeightedAvgPrice(adjustedVwapValue);
		company.setVolWeightedAvgPrice(vwapValue);
		company.setVwapAsOfDate(date);
		company.setVwapCurrency(currency);

	}
	
	/**
	 * Loads GTI data for each company ticker 
	 * 
	 * @param Company Object
	 * @return
	 */
	private void loadCompanyGTI(Company company) {
		GovTransparencyIndex gti = gtiService.getLatest(company.getTickerCode());

		if (gti == null)
			return;

		company.setGtiScore(gti.getTotalScore());
		company.setGtiRankChange(gti.getRankChange());
	}
	
	/**
	 * Creates FX index from the currencies conversion CSV file 
	 * This index has all the conversion data for all required currencies  
	 * 
	 * @param Elastic Search indexName and batchSize for bulk saving in ES
	 * @return Boolean
	 */
	@Override
	public Boolean createFXIndex(@Header String indexName, @Header int fxBatchSize) throws IndexerServiceException {

		log.info("Creating FX index");

		FXRecord.resetFXCache();

		String json = "{ \"index\": { }}\n";
		json += "{ \"from\": \"%s\", \"to\": \"%s\", \"day\": \"%s\", \"multiplier\": %s }\n";

		StringBuilder buffer = new StringBuilder();
		int cnt = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(fxFile))) {
			for (String line; (line = br.readLine()) != null;) {
				FXRecord record = FXRecord.parseFXLine(line, indexName);
				if (record == null)
					continue;
				buffer.append(
						String.format(json, record.getFrom(), record.getTo(), record.getDay(), record.getMultiplier()));
				if (cnt % fxBatchSize == 0 && cnt > 0) {
					log.info("FX Processed {} records", cnt);
					indexerService.bulkSave("fxdata", buffer.toString(), indexName);
					buffer.setLength(0);
				}
				cnt++;
			}
			if (buffer.length() > 0)
				indexerService.bulkSave("fxdata", buffer.toString(), indexName);
			buffer.setLength(0);
		} catch (Exception e) {
			errorBeanHelper.addError(new ErrorBean("IndexBuilderServiceImpl:createFXIndex",
					"Trying to create FX conversion index", ErrorBean.ERROR, errorBeanHelper.getStackTrace(e)));
			throw new IndexerServiceException("Trying to create FX conversion index", e);
		}

		log.info("Finished Creating FX index with {} records", cnt);

		return true;
	}
	
	/**
	 * Creates the indexName based on the currency
	 * 
	 * @param Elastic Search indexName 
	 * @param indexing jobId
	 * @return indexName
	 * @throws IndexerServiceException
	 */
	@Override
	public String computeIndexName(@Header String jobId, @Header String indexName) throws IndexerServiceException {
		CurrencyModel currencyModel = currencyService.getNextCurrency();
		if (currencyModel != null) {
			return currencyModel.getCurrencyName()+ "_" + jobId;
		} else{
			errorBeanHelper.addError(new ErrorBean("IndexBuilderServiceImpl:computeIndexName",
					"Please check the Dataabase if any currencies exist for indexing.", ErrorBean.ERROR, ""));
			throw new IndexerServiceException("Please check if IndexName is available.");
		}

	}

}