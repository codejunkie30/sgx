package com.wmsi.sgx.model.charts;

import java.util.List;
import com.google.common.base.Objects;

public class GrowthOverPriorYears {
	
	private List<GrowthOverPriorYear> growthOverPriorYears;

	public List<GrowthOverPriorYear> getGrowthOverPriorYears() {
		return growthOverPriorYears;
	}

	public void setGrowthOverPriorYears(List<GrowthOverPriorYear> growthOverPriorYears) {
		this.growthOverPriorYears = growthOverPriorYears;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("growthOverPriorYears", growthOverPriorYears).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(growthOverPriorYears);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof GrowthOverPriorYears) {
			GrowthOverPriorYears that = (GrowthOverPriorYears) object;
			return Objects.equal(this.growthOverPriorYears, that.growthOverPriorYears);
		}
		return false;
	}
	
	

	
	
	
	
}
