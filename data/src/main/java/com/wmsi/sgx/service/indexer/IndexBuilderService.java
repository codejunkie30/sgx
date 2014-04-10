package com.wmsi.sgx.service.indexer;

import java.io.IOException;
import java.util.List;

import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;

public interface IndexBuilderService{

	void index(CompanyInputRecord input) throws IndexerServiceException;

	List<CompanyInputRecord> getTickers() throws IndexerServiceException;

	void buildAlphaFactors() throws IOException, AlphaFactorServiceException;

}
