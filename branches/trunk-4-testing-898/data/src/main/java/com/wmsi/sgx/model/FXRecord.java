package com.wmsi.sgx.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Header;

import com.google.common.base.Objects;

public class FXRecord {
	
	private static final Logger log = LoggerFactory.getLogger(FXRecord.class);
	
	static DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyyMMdd");
	
	static Map<String,Map<String,List<FXRecord>>> FXCACHE = new HashMap<String,Map<String,List<FXRecord>>>();

	private String day;
	
	private String from;
	
	private String to;
	
	private Double multiplier;
	
	public FXRecord() {}
	
	public FXRecord(String from, String to, String day, Double multiplier) {
		this.from = from;
		this.to = to;
		this.day = day;
		this.multiplier = multiplier;
	}
	
	public FXRecord(String from, String to, String day, String multiplier) {
		this.from = from;
		this.to = to;
		this.day = day;
		this.multiplier = Double.parseDouble(multiplier);
	}
	
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
	
	public static DateTimeFormatter getDayFormat() { return FMT; }
	
	public static synchronized void resetFXCache() { FXCACHE = new HashMap<String,Map<String,List<FXRecord>>>(); }
	
	public static FXRecord parseFXLine(String line, String indexName) {
		try {
			String[] vals = line.replaceAll("\"", "").split(",");
			if (!vals[1].equals(indexName.substring(0,3).toUpperCase())) return null;
			String[] dt = vals[2].split("\\s+")[0].split("/");
			FXRecord record = new FXRecord(vals[0], vals[1], dt[2] + (dt[0].length() == 1 ? "0" + dt[0] : dt[0]) + (dt[1].length() == 1 ? "0" + dt[1] : dt[1]), vals[3]);
			
			
			Map<String,List<FXRecord>> from = FXCACHE.get(vals[0]);
			if (from == null) {
				from = new HashMap<String,List<FXRecord>>();
				FXCACHE.put(vals[0], from);
			}
			
			List<FXRecord> records = from.get(vals[1]);
			if (records == null) {
				records = new ArrayList<FXRecord>();
				from.put(vals[1], records);
			}
			
			records.add(record);
			
			return record;
		}
		catch(Exception e) {log.error("Exception in parseFXLine method " , e);}
		return null;
	}
	
	public static boolean shouldConvert(String cur, @Header String indexName) {
		return cur == null || cur.equals(indexName.substring(0,3).toUpperCase()) ? false : true;
	}
	
	public static FXRecord getFromCache(String from, String to, Date d) {
		
		
		List<FXRecord> records = getFXRecords(from, to);
		if (records == null) return null;
		
		String date = getDayFormat().print(d.getTime());
		for (FXRecord record : records) {
			if (record.getDay().equals(date)) return record;
		}
		
		return null;
	}
	
	public static FXRecord getLatestRate(String from, String to) {
		List<FXRecord> records = getFXRecords(from, to);
		if (records == null || records.isEmpty()) return null;
		Collections.sort(records, FXRecordDayComparator);
		return records.get(0);
	}
	
	public static List<FXRecord> getFXRecords(String from, String to) {
		Map<String,List<FXRecord>> fl = FXCACHE.get(from);
		if (fl == null) return null;
		return fl.get(to);
	}
	
	public static Comparator<FXRecord> FXRecordDayComparator = new Comparator<FXRecord>() {
		public int compare(FXRecord fx1, FXRecord fx2) {
			return ObjectUtils.compare(fx2.getDay(), fx1.getDay()); // ascending order
		}
	};

	
}

