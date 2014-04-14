package com.wmsi.sgx.model.financials;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonRootName("financials")
public class Financials{

	
	@JsonUnwrapped
	private List<Financial> financials;

	public List<Financial> getFinancials() {
		return financials;
	}

	public void setFinancials(List<Financial> financials) {
		this.financials = financials;
	}
}
