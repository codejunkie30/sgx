package com.wmsi.sgx.model.charts;

import com.google.common.base.Objects;

public class CashFlow {
	
	private String absPeriod;
	private double cashOperations;
	private double cashInvesting;
	private double cashFinancing;
	private double netChange;
	
	
	
	public String getAbsPeriod() {
		return absPeriod;
	}
	public void setAbsPeriod(String absPeriod) {
		if(absPeriod.substring(0,3).equalsIgnoreCase("LTM")){
			this.absPeriod = absPeriod.substring(0,3) + (absPeriod.substring(absPeriod.length()-4));
		}
		else 
			this.absPeriod=absPeriod;
	}
	
	public double getCashOperations() {
		return cashOperations;
	}
	public void setCashOperations(double cashOperations) {
		this.cashOperations = cashOperations;
	}
	public double getCashInvesting() {
		return cashInvesting;
	}
	public void setCashInvesting(double cashInvesting) {
		this.cashInvesting = cashInvesting;
	}
	public double getCashFinancing() {
		return cashFinancing;
	}
	public void setCashFinancing(double cashFinancing) {
		this.cashFinancing = cashFinancing;
	}
	public double getNetChange() {
		return netChange;
	}
	public void setNetChange(double netChange) {
		this.netChange = netChange;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("absPeriod", absPeriod).add("cashOperations", cashOperations)
				.add("cashInvesting", cashInvesting).add("cashFinancing", cashFinancing).add("netChange", netChange)
				.toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(absPeriod, cashOperations, cashInvesting, cashFinancing, netChange);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof CashFlow) {
			CashFlow that = (CashFlow) object;
			return Objects.equal(this.absPeriod, that.absPeriod)
					&& Objects.equal(this.cashOperations, that.cashOperations)
					&& Objects.equal(this.cashInvesting, that.cashInvesting)
					&& Objects.equal(this.cashFinancing, that.cashFinancing)
					&& Objects.equal(this.netChange, that.netChange);
		}
		return false;
	}
	
}
