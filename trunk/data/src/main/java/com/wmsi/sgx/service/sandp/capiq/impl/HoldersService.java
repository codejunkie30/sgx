package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

public class HoldersService extends AbstractDataService {

	private ClassPathResource template = new ClassPathResource("META-INF/query/capiq/holderDetails.json");
	
	@Override
	public Holders load(String id, String... parms) throws CapIQRequestException, ResponseParserException {
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);

		return executeRequest(new CapIQRequest(template), ctx);
	}
}
