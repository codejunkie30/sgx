package com.wmsi.sgx.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.wmsi.sgx.model.histogram.CompanyHistogram;

@JsonRootName("screener")
public class ScreenerResponse{

	private CompanyHistogram histograms;

	public CompanyHistogram getHistograms() {
		return histograms;
	}
	public void setHistograms(CompanyHistogram histograms) {
		this.histograms = histograms;
	}
	public List<String> getIndustries() {
		return industries;
	}
	public void setIndustries(List<String> industries) {
		this.industries = industries;
	}
	private List<String> industries;
	
}
