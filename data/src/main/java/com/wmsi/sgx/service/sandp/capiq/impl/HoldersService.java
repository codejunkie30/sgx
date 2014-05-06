package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@Service
public class HoldersService{

	@Autowired
	private CapIQRequestExecutor requestExecutor;

	private HoldersResponseParser holdersResponseParser = new HoldersResponseParser();
	
	private CapIQRequest holdersRequest(){
		return new CapIQRequest(new ClassPathResource("META-INF/query/capiq/holderDetails.json"));		
	}
	
	public Holders loadHolders(String id) throws CapIQRequestException, ResponseParserException {
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);

		CapIQResponse response = requestExecutor.execute(holdersRequest(), ctx);
		
		return holdersResponseParser.convert(response);
	}
}
