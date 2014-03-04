package com.wmsi.sgx.model.search;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("results")
public class SearchResults{

	private List<SearchCompany> companies;
	public List<SearchCompany> getCompanies(){return companies;}
	public void setCompanies(List<SearchCompany> r){companies = r;}
}
