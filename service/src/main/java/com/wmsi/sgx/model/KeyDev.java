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
	public int hashCode(){
		return Objects.hashCode(date, headline, situation, time);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof KeyDev) {
			KeyDev that = (KeyDev) object;
			return Objects.equal(this.date, that.date)
				&& Objects.equal(this.headline, that.headline)
				&& Objects.equal(this.situation, that.situation)
				&& Objects.equal(this.time, that.time);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("date", date)
			.add("headline", headline)
			.add("situation", situation)
			.add("time", time)
			.toString();
	}
}
