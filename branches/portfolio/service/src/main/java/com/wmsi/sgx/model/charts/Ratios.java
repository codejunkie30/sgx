package com.wmsi.sgx.model.charts;

import java.util.List;
import com.google.common.base.Objects;

public class Ratios {

	private List<Ratio> ratios;

	public List<Ratio> getRatios() {
		return ratios;
	}

	public void setRatios(List<Ratio> ratios) {
		this.ratios = ratios;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("ratios", ratios).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(ratios);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Ratios) {
			Ratios that = (Ratios) object;
			return Objects.equal(this.ratios, that.ratios);
		}
		return false;
	}
	
	
}
