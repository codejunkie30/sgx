package com.wmsi.sgx.service.indexer;

import java.io.IOException;

import com.wmsi.sgx.model.indexer.Indexes;

public interface IndexerService{

	Boolean createIndex(String indexName) throws IOException, IndexerServiceException;

	Boolean createIndexAlias(String indexName) throws IndexerServiceException;

	Boolean save(String type, String id, Object obj, String indexName) throws IndexerServiceException;
	
	Boolean bulkSave(String type, String body, String indexName) throws IndexerServiceException;

	Indexes getIndexes() throws IndexerServiceException;

	Boolean deleteIndex(String indexName) throws IndexerServiceException;
	
	IndexQueryResponse query(String endpoint, String json) throws IndexerServiceException;
	
	
}