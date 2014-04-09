package com.wmsi.sgx.model;

import java.util.Date;
import com.google.common.base.Objects;

public class KeyDev{

	private Date date;
	private String headline;
	private String situation;
	private String time;
	public Date getDate() {
		return date;
	}
	public String getHeadline() {
		return headline;
	}
	public String getSituation() {
		return situation;
	}
	public String getTime() {
		return time;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public void setSituation(String situation) {
		this.situation = situation;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("headline", headline)
			.add("date", date)
			.add("time", time)
			.add("situation", situation)
			.toString();
	}
}
