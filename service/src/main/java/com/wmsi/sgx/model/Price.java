package com.wmsi.sgx.model;

import java.util.Date;

public class Price{

	private Double lastPrice;
	public Double getLastPrice(){return lastPrice;}
	public void setLastPrice(Double p){lastPrice = p;}
	
	private Double openPrice;
	public Double getOpenPrice(){return openPrice;}
	public void setOpenPrice(Double p){openPrice = p;}
	
	private Double closePrice;
	public Double getClosePrice(){return closePrice;}
	public void setClosePrice(Double p){closePrice = p;}

	private Double change;
	public Double getChange(){return change;}
	public void setChange(Double c){change = c;}

	private Double percentChange;
	public Double getPercentChange(){return percentChange;}
	public void setPercentChange(Double p){percentChange = p;}
	
	private Date previousDate;
	public Date getPreviousDate(){return previousDate;}
	public void setPreviousDate(Date d){previousDate = d;}
	
	private Date currentDate;	
	public Date getCurrentDate(){return currentDate;}
	public void setCurrentDate(Date d){currentDate = d;}	
}
