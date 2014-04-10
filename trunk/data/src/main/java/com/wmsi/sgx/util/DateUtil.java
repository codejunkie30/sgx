package com.wmsi.sgx.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;

public class DateUtil{

	private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

	private static final String DEFAULT_DATE_FMT = "MM/dd/yyyy";
	
	public static String adjustDate(String date, int field, int amount) throws CapIQRequestException {
		Date currentDate = toDate(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(field, amount);
		return fromDate(cal.getTime());
	}

	public static String fromDate(Date d) {
		SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_FMT);
		return df.format(d);
	}

	public static Date toDate(String d){
		SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_FMT);
		try{
			return df.parse(d);
		}
		catch(ParseException e){
			log.error("Could not parse string to date", e);
			return null;
		}
	}

}
