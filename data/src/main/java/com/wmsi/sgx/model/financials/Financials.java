package com.wmsi.sgx.model.financials;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonRootName("financials")
public class Financials{

	
	@JsonUnwrapped
	private List<CompanyFinancial> financials;

	public List<CompanyFinancial> getFinancials() {
		return financials;
	}

	public void setFinancials(List<CompanyFinancial> financials) {
		this.financials = financials;
	}
}
