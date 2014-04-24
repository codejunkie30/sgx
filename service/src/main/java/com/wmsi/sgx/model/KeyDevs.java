package com.wmsi.sgx.model;

import java.util.List;
import com.google.common.base.Objects;

public class KeyDevs{

	private String tickerCode;
	private List<KeyDev> keyDevs;

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	public List<KeyDev> getKeyDevs() {
		return keyDevs;
	}

	public void setKeyDevs(List<KeyDev> keyDevs) {
		this.keyDevs = keyDevs;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(tickerCode, keyDevs);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof KeyDevs) {
			KeyDevs that = (KeyDevs) object;
			return Objects.equal(this.tickerCode, that.tickerCode)
				&& Objects.equal(this.keyDevs, that.keyDevs);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("tickerCode", tickerCode)
			.add("keyDevs", keyDevs)
			.toString();
	}
}
