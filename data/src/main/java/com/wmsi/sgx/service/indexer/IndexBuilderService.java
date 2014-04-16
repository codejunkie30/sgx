package com.wmsi.sgx.service.indexer;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.model.integration.CompanyInputRecord;

public interface IndexBuilderService{

	List<CompanyInputRecord> getTickers(String indexName) throws IndexerServiceException;

	CompanyInputRecord index(String indexName, Date jobDate, CompanyInputRecord input);

	Boolean buildAlphaFactors(String indexName);

}
