package com.wmsi.sgx.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Objects;

public class FXRecord {
	
	static DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyyMMdd");

	private String day;
	
	private String from;
	
	private String to;
	
	private Double multiplier;
	
	public String getDay() {
		return day;
	}
	
	public void setDay(String day) {
		this.day = day;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}

	public Double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Double multiplier) {
		this.multiplier = multiplier;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(day, from, to, multiplier);
	}
	
	public boolean matches(String from, String to, Date when) {
		return Objects.equal(from, getFrom()) && Objects.equal(to, getTo()) && Objects.equal(getDayFormat().print(when.getTime()), getDay()); 
	}

	
	@Override
	public boolean equals(Object object){
		if (object instanceof FXRecord) {
			FXRecord that = (FXRecord) object;
			return Objects.equal(this.day, that.day)
				&& Objects.equal(this.from, that.from)
				&& Objects.equal(this.to, that.to)
				&& Objects.equal(this.multiplier, that.multiplier);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("day", day)
			.add("from", from)
			.add("to", to)
			.add("multiplier", multiplier)
			.toString();
	}
	
	public static DateTimeFormatter getDayFormat() {
		return FMT;
	}
	
	public static Object[] parseFXLine(String line) {
		try {
			String[] vals = line.replaceAll("\"", "").split(",");
			if (!vals[1].equals("SGD")) return null;
			String[] dt = vals[2].split(" ")[0].split("/");
			return new Object[] { vals[0], vals[1], dt[2] + (dt[0].length() == 1 ? "0" + dt[0] : dt[0]) + (dt[1].length() == 1 ? "0" + dt[1] : dt[1]), vals[3] };
		}
		catch(Exception e) {}
		return null;
	}
	
	public static boolean shouldConvert(String cur) {
		return cur == null || cur.equals("SGD") ? false : true;
	}

	
}

