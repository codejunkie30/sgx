package com.wmsi.sgx.service.indexer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQServiceException;

public interface IndexBuilderService{

	List<CompanyInputRecord> getTickers(String indexName) throws IndexerServiceException;

	CompanyInputRecord index(String indexName, Date jobDate, CompanyInputRecord input) throws IOException, IndexerServiceException, CapIQRequestException, ParseException, CapIQServiceException;

	Boolean buildAlphaFactors(String indexName) throws AlphaFactorServiceException, IndexerServiceException;

}
