package com.wmsi.sgx.service.sandp.capiq;

import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;

public interface ResponseParser{

	<T> T convert(CapIQResponse response) throws ResponseParserException;

	<T> Class<T> getType();

}
