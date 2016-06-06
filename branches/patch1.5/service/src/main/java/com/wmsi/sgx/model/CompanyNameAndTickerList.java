package com.wmsi.sgx.model;

import java.util.List;
import com.google.common.base.Objects;

public class CompanyNameAndTickerList {
	private List<CompanyNameAndTicker> companyNameAndTickerList;

	public List<CompanyNameAndTicker> getCompanyNameAndTickerList() {
		return companyNameAndTickerList;
	}

	public void setCompanyNameAndTickerList(List<CompanyNameAndTicker> companyNameAndTickerList) {
		this.companyNameAndTickerList = companyNameAndTickerList;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("companyNameAndTickerList", companyNameAndTickerList).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(companyNameAndTickerList);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof CompanyNameAndTickerList) {
			CompanyNameAndTickerList that = (CompanyNameAndTickerList) object;
			return Objects.equal(this.companyNameAndTickerList, that.companyNameAndTickerList);
		}
		return false;
	}
	
	
}
