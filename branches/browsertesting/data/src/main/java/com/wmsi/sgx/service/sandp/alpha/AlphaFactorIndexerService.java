package com.wmsi.sgx.service.sandp.alpha;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.wmsi.sgx.model.AlphaFactor;

public interface AlphaFactorIndexerService{

	List<AlphaFactor> loadAlphaFactors(File file) throws AlphaFactorServiceException;
	
	boolean isAlphaCompany(String id);

	File getLatestFile() throws AlphaFactorServiceException;
	
	File getLatestDownloadedFileFromLocalDirectory() throws IOException;
}