package com.wmsi.sgx.model.account;

import com.google.common.base.Objects;

public class CurrencyModel {
	
	private String currency;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(currency);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof CurrencyModel) {
			CurrencyModel that = (CurrencyModel) object;
			return Objects.equal(this.currency, that.currency);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("currency", currency).toString();
	}
	
}
