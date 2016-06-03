package com.wmsi.sgx.model;

import com.google.common.base.Objects;

public class IsCompanyNonPremiumModel {

	private String exchange;

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("exchange", exchange).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(exchange);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof IsCompanyNonPremiumModel) {
			IsCompanyNonPremiumModel that = (IsCompanyNonPremiumModel) object;
			return Objects.equal(this.exchange, that.exchange);
		}
		return false;
	}
	
}
