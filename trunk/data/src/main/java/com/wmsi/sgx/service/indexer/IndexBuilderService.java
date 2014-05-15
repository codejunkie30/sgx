package com.wmsi.sgx.service.indexer;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

public interface IndexBuilderService{

	List<CompanyInputRecord> getTickers(String indexName) throws IndexerServiceException;

	CompanyInputRecord index(String indexName, Date jobDate, CompanyInputRecord input) throws IndexerServiceException, CapIQRequestException, ResponseParserException;

	Boolean buildAlphaFactors(String indexName) throws AlphaFactorServiceException, IndexerServiceException;

	void deleteOldIndexes() throws IndexerServiceException;

	Boolean isJobSuccessful(List<CompanyInputRecord> records);

}
