package com.wmsi.sgx.service.sandp.capiq;

import java.util.Map;

public interface CapIQRequest{

	String buildQuery(Map<String, Object> ctx) throws CapIQRequestException;
}