package com.wmsi.sgx.service.indexer;

import java.io.IOException;

import com.wmsi.sgx.model.indexer.Indexes;

public interface IndexerService{

	Boolean createIndex(String indexName) throws IOException, IndexerServiceException;

	Boolean createIndexAlias(String indexName) throws IndexerServiceException;

	Boolean save(String type, String id, Object obj, String indexName) throws IndexerServiceException;

	Indexes getIndexes() throws IndexerServiceException;

	Boolean deleteIndex(String indexName) throws IndexerServiceException;
	
	Boolean startBulkIndexing(String indexName) throws IndexerServiceException;
	
	Boolean stopBulkIndexing(String indexName) throws IndexerServiceException, IOException;
	
}