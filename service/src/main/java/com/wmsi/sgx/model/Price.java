package com.wmsi.sgx.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.wmsi.sgx.util.MathUtil;

@JsonRootName(value = "price")
public class Price{

	private final int decimalPlaces = 4;
	
	private Double lastPrice;
	public Double getLastPrice(){return lastPrice;}
	public void setLastPrice(Double p){lastPrice = p;}
	
	private Double openPrice;
	public Double getOpenPrice(){return openPrice;}
	public void setOpenPrice(Double p){openPrice = p;}
	
	private Double closePrice;
	public Double getClosePrice(){return closePrice;}
	public void setClosePrice(Double p){closePrice = p;}

	private Date previousDate;
	public Date getPreviousDate(){return previousDate;}
	public void setPreviousDate(Date d){previousDate = d;}
	
	private Date currentDate;	
	public Date getCurrentDate(){return currentDate;}
	public void setCurrentDate(Date d){currentDate = d;}	

	public Double getChange(){
		Double change = null;
		
		if(closePrice != null && lastPrice != null){
			change = MathUtil.change(closePrice, lastPrice, decimalPlaces);
		}
		
		return change; 		
	}

	public Double getPercentChange(){
		Double change = null;
		
		if(closePrice != null && lastPrice != null){
			return MathUtil.percentChange(closePrice, lastPrice, decimalPlaces);
		}
		
		return change; 		
	}		

}
