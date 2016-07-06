package com.wmsi.sgx.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Objects;

@JsonRootName("gtis")
public class GovTransparencyIndexes{

	private String tickerCode;
	
	@JsonUnwrapped
	private List<GovTransparencyIndex> gtis;

	public List<GovTransparencyIndex> getGtis() {
		return gtis;
	}

	public void setGtis(List<GovTransparencyIndex> gtis) {
		this.gtis = gtis;
	}

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(tickerCode, gtis);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof GovTransparencyIndexes) {
			GovTransparencyIndexes that = (GovTransparencyIndexes) object;
			return Objects.equal(this.tickerCode, that.tickerCode)
				&& Objects.equal(this.gtis, that.gtis);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("tickerCode", tickerCode)
			.add("gtis", gtis)
			.toString();
	}

}
