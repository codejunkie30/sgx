package com.wmsi.sgx.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {

	private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

	private static final String DEFAULT_DATE_FMT = "MM/dd/yyyy";

	public static String adjustDate(String date, int field, int amount) {
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

	public static Date toDate(String d) {
		return toDate(d, DEFAULT_DATE_FMT);
	}

	public static Date toDate(String d, String fmt) {
		SimpleDateFormat df = new SimpleDateFormat(fmt);

		try {
			return df.parse(d);
		} catch (ParseException e) {
			log.error("Could not parse string to date", e);
			return null;
		}
	}

	/**
	 * @param ret
	 */
	public static long getDaysRemaining(Date expirationDate) {
		long diff = resetTimeStamp(expirationDate).getTime() - resetTimeStamp(new Date()).getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public static Date resetTimeStamp(Date expirationDate) {
		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(expirationDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

}
