package com.wmsi.sgx.model.search;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;


public class CompanySearchRequest{

	@NotEmpty	
	@Size(min = 1, max = 128, message="Invaid length: Max 128")
	@SafeHtml
	private String companyName;
	
	public String getCompanyName(){return companyName;}
	public void setCompanyName(String n){companyName = n;}
	
}
