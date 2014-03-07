package com.wmsi.sgx.model.screener;

import com.google.common.base.Objects;

public class Industry{

	private String industry;
	private String industryGroup;
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getIndustryGroup() {
		return industryGroup;
	}
	public void setIndustryGroup(String industryGroup) {
		this.industryGroup = industryGroup;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("industry", industry)
			.add("industryGroup", industryGroup)
			.toString();
	}
}
