package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.financials.Financials;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.CapIQServiceException;
import com.wmsi.sgx.service.sandp.capiq.InvalidIdentifierException;

@Service
public class FinancialsService{

	@Autowired
	private CapIQRequestExecutor requestExecutor;

	@Autowired
	private FinancialsResponseParser finanicalsResponseParser;

	public CapIQRequest companyFinancialsRequest(){
		return new CapIQRequest(new ClassPathResource("META-INF/query/capiq/companyFinancials.json"));		
	}

	public Financials getCompanyFinancials(String id, String currency) throws CapIQRequestException, CapIQServiceException, InvalidIdentifierException {
		return executeRequest(id, currency);
	}
	
	private Financials executeRequest(String id, String currency) throws CapIQRequestException, CapIQServiceException, InvalidIdentifierException {
		Map<String, Object> ctx = buildContext(id, currency);
		CapIQResponse response = requestExecutor.execute(companyFinancialsRequest(), ctx);

		return finanicalsResponseParser.convert(response);
	}
	
	private Map<String, Object> buildContext(String id, String currency){
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("currency", currency);		
		return ctx;
	}
}
