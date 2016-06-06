package com.wmsi.sgx.controller.error;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "error")
public class ErrorResponse extends HashMap<String, Object>{

	private static final long serialVersionUID = 1L;

	public ErrorResponse(ErrorMessage m){
		put("details",m);		
	}
}
