package com.wmsi.sgx.service.indexer;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

public interface IndexBuilderService{
	
	//Index individual CompanyInputRecord into ES Index 
	CompanyInputRecord index(String indexName, CompanyInputRecord input) throws IndexerServiceException, CapIQRequestException, ResponseParserException;
	
	//Create AlphaFactos from AlphaFactor file provided by S&P
	Boolean buildAlphaFactors(String indexName) throws AlphaFactorServiceException, IndexerServiceException;
	
	//Delete old stored indices from ES based on a threshold of number of days
	void deleteOldIndexes() throws IndexerServiceException;

	//Checks if data load was successful
	int isJobSuccessful(List<CompanyInputRecord> records, String indexName) throws IndexerServiceException;
	
	//Reads all the tickets and created List of CompanyInputRecords from it 
	List<CompanyInputRecord> readTickers(String indexName, Date jobDate) throws IndexerServiceException;
	
	//Creates FX index from the currencies conversion CSV file into ES
	Boolean createFXIndex(String indexName, int fxBatchSize) throws IndexerServiceException;
	
	//Generates previous day Index name based on the current day's index date
	String getPreviousDayIndexName(String indexName) throws IndexerServiceException;
	
	//Creates the indexName based on the currency
	String computeIndexName(String jobId, String indexName) throws IndexerServiceException;
	
	//Saves List of available currencies from CSV files
	boolean saveCurrencyList()throws IndexerServiceException;

}
