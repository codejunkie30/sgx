package com.wmsi.sgx.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonRootName("estimates")
public class Estimates {
	
	@JsonUnwrapped
	private List<Estimate> estimates;

	public List<Estimate> getEstimates() {
		return estimates;
	}

	public void setEstimates(List<Estimate> estimates) {
		this.estimates = estimates;
	}
}
