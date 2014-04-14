package com.wmsi.sgx.service.indexer;

import java.io.IOException;
import java.util.List;

import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;

public interface IndexBuilderService{

	void buildAlphaFactors() throws IOException, AlphaFactorServiceException;

	List<CompanyInputRecord> getTickers(String indexName) throws IndexerServiceException;

	CompanyInputRecord index(String indexName, CompanyInputRecord input) throws IndexerServiceException;

	void createAlias(String indexName) throws IndexerServiceException;

}
