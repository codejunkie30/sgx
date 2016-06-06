package com.wmsi.sgx.service.gti;

import com.wmsi.sgx.model.GovTransparencyIndex;
import com.wmsi.sgx.model.GovTransparencyIndexes;

public interface GtiService{

	GovTransparencyIndexes getForTicker(String ticker);

	GovTransparencyIndex getLatest(String ticker);
	
}
