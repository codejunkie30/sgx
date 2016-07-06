package com.wmsi.sgx.model;

import java.util.List;
import com.google.common.base.Objects;

public class Holders{

	private List<Holder> holders;
	private String tickerCode;

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	public List<Holder> getHolders() {
		return holders;
	}

	public void setHolders(List<Holder> holders) {
		this.holders = holders;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(holders, tickerCode);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Holders) {
			Holders that = (Holders) object;
			return Objects.equal(this.holders, that.holders)
				&& Objects.equal(this.tickerCode, that.tickerCode);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("holders", holders)
			.add("tickerCode", tickerCode)
			.toString();
	}	
}
