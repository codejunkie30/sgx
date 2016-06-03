package com.wmsi.sgx.model;

import java.util.List;
import com.google.common.base.Objects;

public class Financials{

	private List<Financial> financials;

	public List<Financial> getFinancials() {
		return financials;
	}

	public void setFinancials(List<Financial> financials) {
		this.financials = financials;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(financials);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Financials) {
			Financials that = (Financials) object;
			return Objects.equal(this.financials, that.financials);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("financials", financials)
			.toString();
	}
}
