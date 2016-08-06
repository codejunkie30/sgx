package com.wmsi.sgx.service.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.service.UtilService;

@Service
public class UtilServiceImpl implements UtilService {
	
	private static final Logger log = LoggerFactory.getLogger(UtilServiceImpl.class);
	
	@Override
	public Iterable<CSVRecord> getRecords(String file) {
		Reader in = null;
		try {
			in = new FileReader(file);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException: ", e);
		}
		
		Iterable<CSVRecord> records = null;
		try {
			records = CSVFormat.newFormat(',').parse(in);
		}	catch (IOException e) {
			log.error("IOException XpressFeed: ", e);
		}
		return records;
	}
	
	@Override
	public Map<Object, Object> convertCurrencyCSVtoMap(String file){
		Iterable<CSVRecord> records = getRecords(file);
		Map<Object, Object> map = new HashMap<Object, Object>();
		int i=0;
		for(CSVRecord record : records){
			Map<Object, Object> map2 = new HashMap<Object, Object>();
			map2.put("id",record.get(0));
			map2.put("name",record.get(1));
			map.put(i,map2);
			i++;
		}
		
		return map;
	}
}
