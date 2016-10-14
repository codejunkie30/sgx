package com.wmsi.sgx.service.sandp.capiq;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.model.FXRecord;
import com.wmsi.sgx.model.annotation.FXAnnotation;
import com.wmsi.sgx.model.annotation.MillionFormatterAnnotation;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.indexer.IndexerService;
import com.wmsi.sgx.service.indexer.IndexerServiceException;
import com.wmsi.sgx.service.sandp.capiq.impl.CSVHelperUtil;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestImpl;


public abstract class AbstractDataService implements DataService{
	
	private static final Logger log = LoggerFactory.getLogger(AbstractDataService.class);
	
	@Value("${loader.companies.dir}")
	private String companiesBase;
	
	@Value("${loader.fx.daysBack}")
	private int daysBack;
	
	protected RequestExecutor requestExecutor;
	public void setRequestExecutor(RequestExecutor e){requestExecutor = e;}
	
	protected ResponseParser responseParser;
	public void setResponseParser(ResponseParser p) {responseParser = p;}
	
	@Autowired
	private IndexerService indexerService;
	
	protected static String DT_FMT_STR = "MM/dd/yyyy";
	
	private static FastDateFormat DT_FMT = FastDateFormat.getInstance(DT_FMT_STR);

	@Override
	public abstract <T> T load(String id, String... parms) throws ResponseParserException, CapIQRequestException;
	
	protected <T> T executeRequest(CapIQRequestImpl req, Map<String, Object> ctx) throws ResponseParserException, CapIQRequestException {
		CapIQResponse response = requestExecutor.execute(req, ctx);
		return responseParser.convert(response);
	}
	
	public Iterable<CSVRecord> getCompanyData(String ticker, String type) {
		String path = companiesBase + type + "/" + ticker.replace(":", "-") + ".csv";
		File f = new File(path);
		if (!f.exists()) return null; 
		CSVHelperUtil csvHelperUtil = new CSVHelperUtil();
		Iterable<CSVRecord> records = csvHelperUtil.getRecords(path);
		return records;
	}
	
	public List<CompanyCSVRecord> getParsedCompanyRecords(String ticker, String type) {
		
		Iterable<CSVRecord> records = getCompanyData(ticker, type); 
		
		List<CompanyCSVRecord> ret = new ArrayList<CompanyCSVRecord>();
		for (CSVRecord record : records) {
			CompanyCSVRecord rec = new CompanyCSVRecord();
			rec.setTicker(record.get(0));
			rec.setExchange(record.get(1));
			rec.setName(record.get(2));
			rec.setValue(record.get(3));
			rec.setPeriod(record.get(4));
			if (StringUtils.stripToNull(record.get(5)) != null) rec.setPeriodDate(new Date(record.get(5)));
			rec.setCurrency(record.get(6));
			ret.add(rec);
		}

		return ret;
	}
	
	//used for Financial and Estimates only
	public List<CompanyCSVRecord> getParsedFinacnialRecords(String ticker, String type) {
		
		Iterable<CSVRecord> records = getCompanyData(ticker, type); 
		
		List<CompanyCSVRecord> ret = new ArrayList<CompanyCSVRecord>();
		for (CSVRecord record : records) {
			CompanyCSVRecord rec = new CompanyCSVRecord();
			rec.setTicker(record.get(0));
			rec.setExchange(record.get(1));
			rec.setName(record.get(2));
			rec.setValue(record.get(3));
			rec.setPeriod(record.get(4));
			if (StringUtils.stripToNull(record.get(5)) != null) rec.setPeriodEndDate(new Date(record.get(5)));
			if (StringUtils.stripToNull(record.get(5)) != null) rec.setPeriodDate(new Date(record.get(5)));
			rec.setCurrency(record.get(6));
			ret.add(rec);
		}

		return ret;
	}
	
	// Function to avoid setting industry values for ASEAN companies as per the ASEAN companies requirements
	public List<CompanyCSVRecord> getParsedCompanyRecords_NoIndustryValues(String ticker, String type) {
		
		Iterable<CSVRecord> records = getCompanyData(ticker, type); 
		
		List<CompanyCSVRecord> ret = new ArrayList<CompanyCSVRecord>();
		for (CSVRecord record : records) {
			CompanyCSVRecord rec = new CompanyCSVRecord();
			rec.setTicker(record.get(0));
			rec.setExchange(record.get(1));
			rec.setName(record.get(2));
			if(rec.getExchange() != "SGX" || rec.getExchange() != "CATALIST" ){
				if(rec.getName().equals("industry") || rec.getName().equals("industryGroup")){
					rec.setValue(null);
				}
			}else{
				rec.setValue(record.get(3));
			}
			rec.setPeriod(record.get(4));
			if (StringUtils.stripToNull(record.get(5)) != null) rec.setPeriodDate(new Date(record.get(5)));
			rec.setCurrency(record.get(6));
			ret.add(rec);
		}

		return ret;
	}
	
	/**
	 * get the value for a particular field
	 * @param field
	 * @param records
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public String getFieldValue(Field field, List<CompanyCSVRecord> records) throws ResponseParserException, CapIQRequestException, IndexerServiceException {
		
		CompanyCSVRecord actual = null;
		
		for (CompanyCSVRecord record : records) {
			if (!record.getName().equals(field.getName())) continue;
			if (actual == null || record.getPeriodDate() == null || record.getPeriodDate().after(actual.getPeriodDate())) actual = record;
		}
		
		return getFieldValue(field, actual);
	}
	
	/**
	 * get the value for a particular field
	 * @param field
	 * @param actual
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public String getFieldValue(Field field, CompanyCSVRecord actual) throws ResponseParserException, CapIQRequestException, IndexerServiceException {
		String value = actual == null ? null : StringUtils.stripToNull(actual.getValue());

		// nothing to do
		if (value == null) return null;
		
		// fx convert
		if (field.isAnnotationPresent(FXAnnotation.class)) value = getFXConverted(value, actual, indexerService.getIndexName());
		
		return value;
	}
	
	public Date getFieldDate(Field field, List<CompanyCSVRecord> records) throws ResponseParserException, CapIQRequestException, IndexerServiceException {
		if (!field.getType().isAssignableFrom(Date.class)) return null;

		CompanyCSVRecord actual = null;
		
		for (CompanyCSVRecord record : records) {
			if (!record.getName().equals(field.getName())) continue;
			if (actual == null) actual = record;
		}
		
		return getDateValue(field, actual);
	}
	
	public Date getDateValue(Field field, CompanyCSVRecord actual) throws ResponseParserException, CapIQRequestException, IndexerServiceException {
		return actual == null || StringUtils.stripToNull(actual.getValue()) != null ? null : actual.getPeriodDate();
	}

	
	public void processMillionRangeValues(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();

		for (Field field : fields) {
			try {
				field.setAccessible(true);
				Double val = processMillionFormatterAnnotation(field, field.get(obj));
				if (val == null)
					continue;
				field.set(obj, val);
			} catch (Exception e) {
				log.error("Getting field val: " + field.getName(), e);
			}
		}
	}

	public Double processMillionFormatterAnnotation(Field field, Object value) throws ResponseParserException, CapIQRequestException, IndexerServiceException {
		Double ret = null;
		// nothing to do
		if (value == null) return null;
		
		if (field.isAnnotationPresent(MillionFormatterAnnotation.class)) ret = ((Double)value)/1000000.0;

		return ret;
	}
	
	public Double processMillionFormatter(Object value) throws ResponseParserException, CapIQRequestException, IndexerServiceException {
		if (value == null) return null;
		return ((Double)value)/1000000.0;
	}
	
	/**
	 * convert the value of csv record (assumes it can be converted)
	 * @param actual
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public String getFXConverted(String value, CompanyCSVRecord actual, String indexName) throws ResponseParserException, CapIQRequestException, IndexerServiceException {
		String dataTobeConvertedInCurrency = indexName.substring(0,3).toUpperCase();
		// can't convert
		if (value == null || actual.getCurrency() == null || actual.getPeriodDate() == null) return value;
		
		// temp hack for just processing SGD
		if (!FXRecord.shouldConvert(actual.getCurrency(), indexName)) return actual.getValue();

		// pull it from the cache
		FXRecord record = FXRecord.getFromCache(actual.getCurrency(), dataTobeConvertedInCurrency, actual.getPeriodDate());
		
		// offtime runs, might get future dates we can't convert grab the latest we have
		if (record == null) record = FXRecord.getLatestRate(actual.getCurrency(), dataTobeConvertedInCurrency);
		
		// get the value
		if (record != null) {
			BigDecimal val = BigDecimal.valueOf(record.getMultiplier()).multiply(new BigDecimal(actual.getValue()));
			return val.toString();
		}
		
		throw new ResponseParserException("No conversion available for field " + actual);
		
	}
	
}
