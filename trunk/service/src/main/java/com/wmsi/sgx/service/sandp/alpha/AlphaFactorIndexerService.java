package com.wmsi.sgx.service.sandp.alpha;

import java.io.File;
import java.util.List;

import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;

public interface AlphaFactorIndexerService{

	List<AlphaFactor> loadAlphaFactors(File file);
}