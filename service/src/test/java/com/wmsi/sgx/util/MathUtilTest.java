package com.wmsi.sgx.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MathUtilTest{

	@Test
	public void testPercentChange(){
		Double change = MathUtil.percentChange(15.59, 15.21, 4);
		Assert.assertEquals(change, -2.437D);
	}

}
