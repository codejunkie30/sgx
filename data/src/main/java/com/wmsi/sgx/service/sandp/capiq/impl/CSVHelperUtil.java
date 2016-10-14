package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVHelperUtil {
	
	private static final Logger log = LoggerFactory.getLogger(CSVHelperUtil.class);
	
	static Map<String,Iterable<CSVRecord>> CACHE = new HashMap<String,Iterable<CSVRecord>>();
	
	/**
	 * Utility method to get records from data file
	 * @param data file 
	 * @return CSVRecord
	 *
	 */
	public Iterable<CSVRecord> getRecords(String file) {
		ArrayList<CSVRecord> ret = new ArrayList<CSVRecord>();
		Reader in = null;
		CSVParser parsed = null;
		try {
			in = new FileReader(file);
			parsed = CSVFormat.DEFAULT.parse(in);
			for (CSVRecord record: parsed) ret.add(record);
			return ret;
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException CSVHelperUtil: ", e);
		} catch (IOException e) {
			log.error("IOException CSVHelperUtil: ", e);
		}
		finally {
			try { if (parsed != null) parsed.close(); }
			catch(Exception e) {}
		}
		return null;
	}

}
