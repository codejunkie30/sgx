package com.wmsi.sgx.service.sandp.capiq;


public interface DataService{

	<T> T load(String id, String... parms) throws ResponseParserException, CapIQRequestException;
}
