package com.wmsi.sgx.service.sandp.capiq;

import java.util.Map;

import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.impl.CapIQRequestImpl;


public abstract class AbstractDataService implements DataService{

	protected RequestExecutor requestExecutor;
	public void setRequestExecutor(RequestExecutor e){requestExecutor = e;}
	
	protected ResponseParser responseParser;
	public void setResponseParser(ResponseParser p) {responseParser = p;}

	@Override
	public abstract <T> T load(String id, String... parms) throws ResponseParserException, CapIQRequestException;
	
	protected <T> T executeRequest(CapIQRequestImpl req, Map<String, Object> ctx) throws ResponseParserException, CapIQRequestException {
		CapIQResponse response = requestExecutor.execute(req, ctx);
		return responseParser.convert(response);
	}
}
