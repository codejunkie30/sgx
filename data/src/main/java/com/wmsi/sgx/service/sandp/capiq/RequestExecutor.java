package com.wmsi.sgx.service.sandp.capiq;

import java.util.Map;

import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestImpl;

public interface RequestExecutor{

	CapIQResponse execute(CapIQRequestImpl req, Map<String, Object> ctx) throws CapIQRequestException;
}
