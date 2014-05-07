package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import com.wmsi.sgx.model.financials.Financials;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

public class FinancialsService extends AbstractDataService {

	private ClassPathResource template = new ClassPathResource("META-INF/query/capiq/companyFinancials.json");
	
	@Override
	public Financials load(String id, String... parms) throws ResponseParserException, CapIQRequestException {
		
		Assert.notEmpty(parms);
		
		Map<String, Object> ctx = buildContext(id, parms[0]);		
		return executeRequest(new CapIQRequest(template), ctx);
	}
	
	private Map<String, Object> buildContext(String id, String currency){
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("currency", currency);		
		return ctx;
	}
}
