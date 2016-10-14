package com.wmsi.sgx.service.sandp.alpha;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.wmsi.sgx.model.AlphaFactor;

public interface AlphaFactorIndexerService{
	
	//Loads AlphaFactors from Latest AlphaFactor file
	List<AlphaFactor> loadAlphaFactors(File file) throws AlphaFactorServiceException;
	
	//Check if an id is valid Alpha Company
	boolean isAlphaCompany(String id);
	
	//Retrieve latest AlphaFactor file
	File getLatestFile() throws AlphaFactorServiceException;
	
	//Utility method to get latest downloaded file from local Directory
	File getLatestDownloadedFileFromLocalDirectory() throws IOException;
}