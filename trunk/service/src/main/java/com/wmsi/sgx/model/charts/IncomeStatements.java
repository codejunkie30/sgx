package com.wmsi.sgx.model.charts;

import java.util.List;
import com.google.common.base.Objects;

public class IncomeStatements {

	private List<IncomeStatement> incomeStatements;

	public List<IncomeStatement> getIncomeStatements() {
		return incomeStatements;
	}

	public void setIncomeStatements(List<IncomeStatement> incomeStatements) {
		this.incomeStatements = incomeStatements;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(incomeStatements);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof IncomeStatements) {
			IncomeStatements that = (IncomeStatements) object;
			return Objects.equal(this.incomeStatements, that.incomeStatements);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("incomeStatements", incomeStatements).toString();
	}
	
	
}
