package com.wmsi.sgx.model;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PriceTest{

	@DataProvider
	public Object[][] getPrice(){
		
		Price p = new Price();
		p.setClosePrice(10.17);
		p.setLastPrice(10.14);
		p.setOpenPrice(10.20);

		return new Object[][]{{p}};
	}
	
	@Test(dataProvider="getPrice")
	public void testChange(Price p){

		assertNotNull(p);
		assertEquals(p.getPercentChange(), -0.295D);
		assertEquals(p.getChange(), -0.03D);
	}
}
