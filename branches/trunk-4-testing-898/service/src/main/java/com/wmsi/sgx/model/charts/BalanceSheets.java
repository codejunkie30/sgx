package com.wmsi.sgx.model.charts;

import java.util.List;
import com.google.common.base.Objects;

public class BalanceSheets {
	
	private List<BalanceSheet> balanceSheet;

	public List<BalanceSheet> getBalanceSheet() {
		return balanceSheet;
	}

	public void setBalanceSheet(List<BalanceSheet> balanceSheet) {
		this.balanceSheet = balanceSheet;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(balanceSheet);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof BalanceSheets) {
			BalanceSheets that = (BalanceSheets) object;
			return Objects.equal(this.balanceSheet, that.balanceSheet);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("balanceSheet", balanceSheet).toString();
	}
	
	
}
