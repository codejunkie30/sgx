package com.wmsi.sgx.service.indexer;

import java.io.IOException;

import org.springframework.integration.annotation.Header;

import com.wmsi.sgx.model.indexer.Indexes;

public interface IndexerService{
	
	//Creates index name from Spring's Header variable indexName
	Boolean createIndex(String indexName) throws IOException, IndexerServiceException;
	
	//Creates IndexAlias from Spring's Header variable indexName
	Boolean createIndexAlias(String indexName) throws IndexerServiceException;
	
	//Saves data into specific ES bucket based on type of data 
	Boolean save(String type, String id, Object obj, String indexName) throws IndexerServiceException;
	
	//bulk saves data into specific ES bucket based on type of data 
	Boolean bulkSave(String type, String body, String indexName) throws IndexerServiceException;
	
	//Retrieves list of all available indices 
	Indexes getIndexes() throws IndexerServiceException;
	
	//Deletes ES Index
	Boolean deleteIndex(String indexName) throws IndexerServiceException;
	
	//Skeleton for running any ES Query against an endpoint
	IndexQueryResponse query(String endpoint) throws IndexerServiceException;
	
	//Flushes the ES index
	Boolean flush() throws IndexerServiceException;
	
	//Retrieves current index name
	String getIndexName();
	
}