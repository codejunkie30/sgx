package com.wmsi.sgx.service.indexer;

import java.io.IOException;

public interface IndexerService{

	Boolean createIndex(String indexName) throws IOException, IndexerServiceException;

	Boolean createIndexAlias(String indexName) throws IndexerServiceException;

	Boolean save(String type, String id, Object obj, String indexName) throws IndexerServiceException;

}