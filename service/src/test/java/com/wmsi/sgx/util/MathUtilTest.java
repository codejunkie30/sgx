package com.wmsi.sgx.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MathUtilTest{

	@Test
	public void testPercentChange(){
		Double change = MathUtil.percentChange(15.59, 15.21, 4);
		Assert.assertEquals(change, -2.4375);
	}

	@Test
	public void testChange(){
		Double change = MathUtil.change(15.59, 15.21, 4);
		Assert.assertEquals(change, -0.38);
	}
	
	@Test
	public void testAvg(){
		Double sum = 12345.6789;
		Integer total = 43;		
		Double avg = MathUtil.avg(sum, total, 4);
		Assert.assertEquals(avg, 287.1088);
	}

}
