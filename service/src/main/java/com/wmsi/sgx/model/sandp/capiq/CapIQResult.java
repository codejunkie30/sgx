package com.wmsi.sgx.model.sandp.capiq;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CapIQResult{

	@JsonProperty("Headers")
	private String headers;
	public String getHeaders(){return headers;}
	public void setHeaders(String h){headers = h;}
	
	@JsonProperty("Rows")
	private List<CapIQRow> rows;
	public List<CapIQRow> getRows(){return rows;}
	public void setRows(List<CapIQRow> r){rows = r;}
	
	@JsonProperty("Mnemonic")
	private String mnemonic;
	public String getMnemonic(){return mnemonic;}
	public void setMnemonic(String m){mnemonic = m;}
}
