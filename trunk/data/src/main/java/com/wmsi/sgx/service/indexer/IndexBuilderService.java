package com.wmsi.sgx.service.indexer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;

public interface IndexBuilderService{

	String buildIndex() throws IOException, URISyntaxException, IndexerServiceException, CapIQRequestException,
			ParseException, AlphaFactorServiceException;

}
