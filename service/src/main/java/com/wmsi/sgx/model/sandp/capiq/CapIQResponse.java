package com.wmsi.sgx.model.sandp.capiq;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CapIQResponse{
	
	@JsonProperty(value="GDSSDKResponse")
	private List<CapIQResult> results;
	
	public List<CapIQResult> getResults(){return results;}
	public void setResults(List<CapIQResult> r){results = r;}	
	
}
