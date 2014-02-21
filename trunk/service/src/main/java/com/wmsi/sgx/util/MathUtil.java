package com.wmsi.sgx.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtil{
	
	/**
	 * Calculate the percent change between two numbers
	 * @param previous - Starting value
	 * @param current - Current value
	 * @return percent change between values
	 */
	public static Double percentChange(Double previous, Double current, Integer scale){
		MathContext ctx = new MathContext(scale,RoundingMode.HALF_UP);
		BigDecimal curr = new BigDecimal(current,ctx);
		BigDecimal prev = new BigDecimal(previous, ctx);

		// Formula ((current - previous) / previous) * 100
		BigDecimal change = curr.subtract(prev).divide(prev, ctx);
		change = change.multiply(BigDecimal.valueOf(100)); 
				
		return change.doubleValue();
	}

}
