package com.wmsi.sgx.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil{
	
	/**
	 * Calculate the percent change between two numbers
	 * @param previous - Starting value
	 * @param current - Current value
	 * @param scale - Number of decimal places to round too
	 * @return percent change between values
	 */
	public static Double percentChange(Double previous, Double current, int scale){
		BigDecimal curr = new BigDecimal(current);
		BigDecimal prev = new BigDecimal(previous);

		// Formula ((current - previous) / previous) * 100	
		BigDecimal change = curr.subtract(prev).divide(prev, RoundingMode.HALF_UP);
		change = change.multiply(BigDecimal.valueOf(100)); 
				
		return change.setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Calculate the change between two numbers with precision
	 * @param previous - Starting value
	 * @param current - Current value
	 * @param scale - number of decimal places
	 * @return difference
	 */
	public static Double change(Double previous, Double current, int scale){
		BigDecimal curr = new BigDecimal(current);
		BigDecimal prev = new BigDecimal(previous);
		return curr.subtract(prev).setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}	
}
