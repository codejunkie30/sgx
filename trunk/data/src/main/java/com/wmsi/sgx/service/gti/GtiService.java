package com.wmsi.sgx.service.gti;

import com.wmsi.sgx.model.GovTransparencyIndex;
import com.wmsi.sgx.model.GovTransparencyIndexes;

public interface GtiService{
	
	// Get all GTIs for the given ticker
	GovTransparencyIndexes getForTicker(String ticker);
	
	// Get GTI data for latest year for the given ticker.
	GovTransparencyIndex getLatest(String ticker);
	
}
