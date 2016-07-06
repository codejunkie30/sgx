package com.wmsi.sgx.service.sandp.capiq;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.wmsi.sgx.model.HistoricalValue;

public class CompanyCSVRecord {
	
	private String ticker;
	
	private String exchange;
	
	private String name;
	
	private String value;
	
	private String period;
	
	// assume it's the latest date if not set
	private Date periodDate = new Date();
	
	private Date periodEndDate;
	private String currency;
	
	public CompanyCSVRecord() {}
	
	public CompanyCSVRecord(String currency, String value, Date periodDate) {
		this.currency = currency;
		this.value = value;
		this.periodDate = periodDate;
	}

	public String getTicker() {
		return StringUtils.stripToNull(ticker);
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getExchange() {
		return StringUtils.stripToNull(exchange);
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getName() {
		return StringUtils.stripToNull(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return StringUtils.stripToNull(value);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPeriod() {
		return StringUtils.stripToNull(period);
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Date getPeriodDate() {
		return periodDate;
	}

	public void setPeriodDate(Date periodDate) {
		this.periodDate = periodDate;
	}
	
	public String getCurrency() {
		return StringUtils.stripToNull(currency);
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
	public Date getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(Date periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public static Comparator<CompanyCSVRecord> getCompanyCSVRecordDateComparator() {
		return CompanyCSVRecordDateComparator;
	}

	public static void setCompanyCSVRecordDateComparator(Comparator<CompanyCSVRecord> companyCSVRecordDateComparator) {
		CompanyCSVRecordDateComparator = companyCSVRecordDateComparator;
	}

	@Override
	public String toString() {
		return "CompanyCSVRecord [ticker=" + ticker + ", exchange=" + exchange
				+ ", name=" + name + ", value=" + value + ", period=" + period
				+ ", periodDate=" + periodDate + ", periodEndDate=" + periodEndDate + ",currency=" + currency + "]";
	}
	
	public static List<CompanyCSVRecord> getByName(String name, List<CompanyCSVRecord> records) {
		List<CompanyCSVRecord> ret = new ArrayList<CompanyCSVRecord>();
		for (CompanyCSVRecord rec : records) {
			if (name.equals(rec.getName())) ret.add(rec);
		}
		return ret;	
	}
	
	public static Comparator<CompanyCSVRecord> CompanyCSVRecordDateComparator = new Comparator<CompanyCSVRecord>() {
		public int compare(CompanyCSVRecord hv1, CompanyCSVRecord hv2) {
			Date d1 = hv1.getPeriodDate();
			Date d2 = hv2.getPeriodDate();
			return ObjectUtils.compare(d2, d1);
		}

	};
	
}