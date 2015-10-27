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
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.model.FXRecord;
import com.wmsi.sgx.model.annotation.FXAnnotation;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.indexer.IndexQueryResponse;
import com.wmsi.sgx.service.indexer.IndexerService;
import com.wmsi.sgx.service.indexer.IndexerServiceException;
import com.wmsi.sgx.service.sandp.capiq.impl.CSVHelperUtil;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestImpl;


public abstract class AbstractDataService implements DataService{
	
	private static final Logger log = LoggerFactory.getLogger(AbstractDataService.class);
	
	@Value("${loader.companies.dir}")
	private String companiesBase;
	
	protected List<FXRecord> fxRecords;

	protected RequestExecutor requestExecutor;
	public void setRequestExecutor(RequestExecutor e){requestExecutor = e;}
	
	protected ResponseParser responseParser;
	public void setResponseParser(ResponseParser p) {responseParser = p;}
	
	@Autowired
	private IndexerService indexerService;

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
	
	public List<FXRecord> getFXData(String id, List<String> currencies) throws IndexerServiceException {
		
		// not converting SGD for now
		currencies.remove("SGD");
		
		List<FXRecord> records = null;
		
		if (currencies.size() > 0) {
		
			String endpoint = "/fxdata/_search?size=10000&q=from:";
			String terms = "";
	
			for (String s : currencies) {
				if (terms.length() > 0) terms += "%20OR%20";
				terms += s;
			}
			
			IndexQueryResponse iqr = indexerService.query(endpoint + terms);
			records = iqr.getHits(FXRecord.class);
			
		}
		
		if (records == null) records = new ArrayList<FXRecord>();
		
		return records;
	}
	
	/**
	 * get the value for a particular field
	 * @param name
	 * @param records
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public String getFieldValue(Field field, List<CompanyCSVRecord> records) throws ResponseParserException, CapIQRequestException {
		
		CompanyCSVRecord actual = null;
		
		for (CompanyCSVRecord record : records) {
			if (!record.getName().equals(field.getName())) continue;
			if (actual == null || record.getPeriodDate() == null || record.getPeriodDate().after(actual.getPeriodDate())) actual = record;
		}
		
		return getFieldValue(field, actual);
	}
	
	/**
	 * get the value for a particular field
	 * @param name
	 * @param record
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public String getFieldValue(Field field, CompanyCSVRecord actual) throws ResponseParserException, CapIQRequestException {

		String value = actual == null ? null : actual.getValue();
		return value;
		/**
		// nothing to do
		if (value == null) return null;
		
		// fx convert
		if (field.isAnnotationPresent(FXAnnotation.class)) value = getFXConverted(value, actual);
		
		return value;
		*/
	}
	
	/**
	 * convert the value of csv record (assumes it can be converted)
	 * @param actual
	 * @return
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public String getFXConverted(String value, CompanyCSVRecord actual) throws ResponseParserException, CapIQRequestException {

		// can't convert
		if (value == null ||
			fxRecords == null || 
			fxRecords.size() == 0 ||
			actual.getCurrency() == null || 
			actual.getPeriodDate() == null
		) return value;
		
		// temp hack for just processing SGD
		if (actual.getCurrency().equals("SGD")) return actual.getValue();
		
		// let's try and find a match
		for (FXRecord record : fxRecords) {
			if (!record.getFrom().equals(actual.getCurrency()) || !DateUtils.isSameDay(actual.getPeriodDate(), record.getDate())) continue;
			BigDecimal val = BigDecimal.valueOf(record.getMultiplier()).multiply(new BigDecimal(actual.getValue()));
			return val.toString();
		}
		
		// this is for runs that are ahead of the FX conversion data
		// we go back a day - temporary hack
		if (DateUtils.isSameDay(actual.getPeriodDate(), new Date())) {
			for (FXRecord record : fxRecords) {
				if (!record.getFrom().equals(actual.getCurrency()) || !DateUtils.isSameDay(DateUtils.addDays(new Date(), -1), record.getDate())) continue;
				BigDecimal val = BigDecimal.valueOf(record.getMultiplier()).multiply(new BigDecimal(actual.getValue()));
				return val.toString();
			}
		}
		
		throw new ResponseParserException("No conversion available for field " + actual);
		
		
	}
	
}
