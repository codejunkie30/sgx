package com.wmsi.sgx.model.charts;

import java.util.List;
import com.google.common.base.Objects;

public class CashFlows {
	
	private List<CashFlow> cashFlows;

	public List<CashFlow> getCashFlows() {
		return cashFlows;
	}

	public void setCashFlows(List<CashFlow> cashFlows) {
		this.cashFlows = cashFlows;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("cashFlow", cashFlows).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(cashFlows);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof CashFlows) {
			CashFlows that = (CashFlows) object;
			return Objects.equal(this.cashFlows, that.cashFlows);
		}
		return false;
	}
	
	
	
	

}
