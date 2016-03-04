package com.wmsi.sgx.service;

import java.util.Map;

import org.apache.commons.csv.CSVRecord;

public interface UtilService {

	Iterable<CSVRecord> getRecords(String file);
	Map<String,String> convertCurrencyCSVtoMap(String file);
}
