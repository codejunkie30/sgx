package com.wmsi.sgx.service.search.elasticsearch;

import com.google.common.base.Objects;

public class StatAggregation extends DefaultAggregation{

	private Integer count;
	private Double min;
	private Double max;
	private Double avg;
	private Double sum;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer c) {
		count = c;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double m) {
		min = m;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double m) {
		max = m;
	}

	public Double getAvg() {
		return avg;
	}

	public void setAvg(Double a) {
		avg = a;
	}

	public Double getSum() {
		return sum;
	}

	public void setSum(Double s) {
		sum = s;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("super", super.toString())
			.add("count", count)
			.add("min", min)
			.add("max", max)
			.add("avg", avg)
			.add("sum", sum)
			.toString();
	}
}
