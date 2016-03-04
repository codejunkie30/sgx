package com.wmsi.sgx.service;

import java.util.Map;

import org.apache.commons.csv.CSVRecord;

public interface UtilService {

	Iterable<CSVRecord> getRecords(String file);
	Map<Object, Object> convertCurrencyCSVtoMap(String file);
}
