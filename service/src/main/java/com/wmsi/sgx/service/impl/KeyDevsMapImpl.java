package com.wmsi.sgx.service.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import com.wmsi.sgx.service.KeyDevsMap;

@Service
public class KeyDevsMapImpl implements KeyDevsMap{
	ClassPathResource keyDevFile;
	private Map<String, List<String>> keyDevMap;
	private Map<String, String> keyDevLabel;
	private Map<String, String> keyDevTypeLabelMap;
	
	@Override
	public Map<String, List<String>> getMap(){
		return keyDevMap;
	}
	@Override
	public String getKeyDevLabel(String key){
		return keyDevLabel.get(key);
	}
	
	@Override
	public String getKeyDevLabelByType(String type){
		return keyDevTypeLabelMap.get(type);
	}
	
	
	@PostConstruct
	public void initMap() throws IOException{
		keyDevFile = new ClassPathResource("data/keydev.csv");
		CSVReader csvReader = null;
		InputStreamReader reader = null;
		String[] record = null;	
		
		List<String> kdAnounceCompTransactions = new ArrayList<String>();
		List<String> kdCompanyForecasts = new ArrayList<String>();
		List<String> kdCorporateStructureRelated = new ArrayList<String>();
		List<String> kdCustProdRelated = new ArrayList<String>();
		List<String> kdDividensSplits = new ArrayList<String>();
		List<String> kdListTradeRelated = new ArrayList<String>();
		List<String> kdPotentialRedFlags = new ArrayList<String>();
		List<String> kdPotentialTransactions = new ArrayList<String>();
		List<String> kdResultsCorpAnnouncements = new ArrayList<String>();
		
		keyDevTypeLabelMap = new HashMap<>();
		try{
			reader = new InputStreamReader(keyDevFile.getInputStream());
			csvReader = new CSVReader(reader, ',');
			while((record = csvReader.readNext()) != null){
				keyDevTypeLabelMap.put(record[0], record[1]);
				switch(record[1]){
				case "Announced/Completed Transactions":
					kdAnounceCompTransactions.add(record[0]);
				case "Company Forecasts and Ratings":
					kdCompanyForecasts.add(record[0]);
				case "Corporate Structure Related":
					kdCorporateStructureRelated.add(record[0]);
				case "Dividends/Splits":
					kdDividensSplits.add(record[0]);
				case "Customer/Product Related":
					kdCustProdRelated.add(record[0]);
				case "Listing/Trading Related":
					kdListTradeRelated.add(record[0]);
				case "Potential Red Flags/Distress Indicators":
					kdPotentialRedFlags.add(record[0]);
				case "Potential Transactions":
					kdPotentialTransactions.add(record[0]);
				case "Corporate Communications":
					kdResultsCorpAnnouncements.add(record[0]);

				}
			}
		}
		catch(IOException e){
			
		}
		finally{
			IOUtils.closeQuietly(csvReader);
			IOUtils.closeQuietly(reader);
		}
		keyDevMap = new HashMap<String, List<String>>();
		keyDevMap.put("kdAnounceCompTransactions", kdAnounceCompTransactions);
		keyDevMap.put("kdCompanyForecasts", kdCompanyForecasts);
		keyDevMap.put("kdCorporateStructureRelated", kdCorporateStructureRelated);
		keyDevMap.put("kdCustProdRelated", kdCustProdRelated);
		keyDevMap.put("kdDividensSplits", kdDividensSplits);
		keyDevMap.put("kdListTradeRelated", kdListTradeRelated);
		keyDevMap.put("kdPotentialRedFlags", kdPotentialRedFlags);
		keyDevMap.put("kdPotentialTransactions", kdPotentialTransactions);
		keyDevMap.put("kdResultsCorpAnnouncements", kdResultsCorpAnnouncements);
		
		keyDevLabel = new HashMap<String, String>();
		keyDevLabel.put("kdAnounceCompTransactions", "Announced/Completed Transactions");
		keyDevLabel.put("kdCompanyForecasts", "Company Forecasts and Ratings");
		keyDevLabel.put("kdCorporateStructureRelated", "Corporate Structure Related");
		keyDevLabel.put("kdCustProdRelated", "Customer/Product Related");
		keyDevLabel.put("kdDividensSplits", "Dividends/Splits");
		keyDevLabel.put("kdListTradeRelated", "Listing/Trading Related");
		keyDevLabel.put("kdPotentialRedFlags", "Potential Red Flags/Distress Indicators");
		keyDevLabel.put("kdPotentialTransactions", "Potential Transactions");
		keyDevLabel.put("kdResultsCorpAnnouncements", "Corporate Communications");
		
		}
}
