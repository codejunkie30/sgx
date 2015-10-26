package com.wmsi.sgx.service.sandp.capiq;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.model.FXRecord;
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

		/**
		// not converting SGD for now
		currencies.remove("SGD");
		if (currencies.size() == 0) return null;

		String query = "{ \"size\":10000, \"query\":{ \"constant_score\":{ \"filter\":{  \"or\": [ %s ] }}}}";
		String term = "{ \"term\":{ \"from\": \"%s\" } }";
		String terms = "";
		
		for (String s : currencies) {
			if (terms.length() > 0) terms += ",";
			terms += String.format(term, s);
		}
		
		query = String.format(query, terms);
		
		IndexQueryResponse iqr = indexerService.query("/fxdata/_search", query);
		List<FXRecord> records = iqr.getHits(FXRecord.class);
		
		return records;
		*/
		return new ArrayList<FXRecord>();
	}
	
}
