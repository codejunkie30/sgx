package com.wmsi.sgx.model.distribution;

import java.util.List;
import com.google.common.base.Objects;

public class Distributions{

	private List<Distribution> distributions;

	public List<Distribution> getDistributions() {
		return distributions;
	}

	public void setDistributions(List<Distribution> distributions) {
		this.distributions = distributions;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("distributions", distributions)
			.toString();
	}
}
