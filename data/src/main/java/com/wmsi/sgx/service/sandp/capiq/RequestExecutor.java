package com.wmsi.sgx.service.sandp.capiq;

import java.util.Map;

import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;

public interface RequestExecutor{

	CapIQResponse execute(CapIQRequest req, Map<String, Object> ctx) throws CapIQRequestException;

	CapIQResponse execute(String query) throws CapIQRequestException;

}
