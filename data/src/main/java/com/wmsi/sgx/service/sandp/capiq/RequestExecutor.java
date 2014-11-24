package com.wmsi.sgx.service.sandp.capiq;

import java.util.List;
import java.util.Map;

import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestImpl;

public interface RequestExecutor{

	CapIQResponse execute(CapIQRequestImpl req, Map<String, Object> ctx) throws CapIQRequestException;

	void writeFile(String path2, CapIQResponse response);

	List<String> loadFiles(String fileStart, String cachePath);

	CapIQResponse readFile(String path);
}
