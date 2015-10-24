package com.wmsi.sgx.model.search;

import com.google.common.base.Objects;

public class ChartRequestModel {
	
	private String id;
	private ChartDomain chartDomain;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ChartDomain getChartDomain() {
		return chartDomain;
	}
	public void setChartDomain(ChartDomain chartDomain) {
		this.chartDomain = chartDomain;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("chartDomain", chartDomain).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(id, chartDomain);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof ChartRequestModel) {
			ChartRequestModel that = (ChartRequestModel) object;
			return Objects.equal(this.id, that.id) && Objects.equal(this.chartDomain, that.chartDomain);
		}
		return false;
	}
	
	
	
}
