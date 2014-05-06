package com.wmsi.sgx.util;

import static org.testng.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.testng.annotations.Test;

public class DateUtilTest{

	@Test
	public void testFromDate() throws ParseException{
		SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
		String testDate = "04/25/2014";
		Date d = fmt.parse(testDate);
		String date = DateUtil.fromDate(d);
		assertEquals(date, testDate);
	}
	
	@Test
	public void testToDate() throws ParseException{
		SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
		String testDate = "04/25/2014";
		Date d = fmt.parse(testDate);
		Date date = DateUtil.toDate(testDate);
		
		assertEquals(d, date);
	}

	@Test
	public void testAdjustDate(){
		String testDate = "04/25/2014";

		String adjustedDate = DateUtil.adjustDate(testDate, Calendar.YEAR, -1);
		assertEquals(adjustedDate , "04/25/2013");
		
		adjustedDate = DateUtil.adjustDate(testDate, Calendar.YEAR, -5);
		assertEquals(adjustedDate , "04/25/2009");

		adjustedDate = DateUtil.adjustDate(testDate, Calendar.MONTH, -3);
		assertEquals(adjustedDate , "01/25/2014");

		adjustedDate = DateUtil.adjustDate(testDate, Calendar.MONTH, -4);
		assertEquals(adjustedDate , "12/25/2013");

		adjustedDate = DateUtil.adjustDate(testDate, Calendar.DAY_OF_MONTH, -5);
		assertEquals(adjustedDate , "04/20/2014");

		adjustedDate = DateUtil.adjustDate(testDate, Calendar.DAY_OF_MONTH, -26);
		assertEquals(adjustedDate , "03/30/2014");

	}
}
