package com.wmsi.sgx.model;

import java.util.Comparator;
import java.util.Date;

import com.google.common.base.Objects;
import com.wmsi.sgx.model.annotation.FXAnnotation;

public class HistoricalValue {

	private String tickerCode;
	
	private Date date;
	
	@FXAnnotation
	private Double value;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(tickerCode, date, value);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof HistoricalValue) {
			HistoricalValue that = (HistoricalValue) object;
			return Objects.equal(this.tickerCode, that.tickerCode) && Objects.equal(this.date, that.date)
					&& Objects.equal(this.value, that.value);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("tickerCode", tickerCode).add("date", date).add("value", value)
				.toString();
	}

	public static Comparator<HistoricalValue> HistoricalValueComparator = new Comparator<HistoricalValue>() {
		public int compare(HistoricalValue hv1, HistoricalValue hv2) {
			Date d1 = hv1.getDate();
			Date d2 = hv2.getDate();
			// ascending order
			return d2.compareTo(d1);
		}

	};
}
