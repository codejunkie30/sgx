package com.wmsi.sgx.model;

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

	private Double percentChange;
	public Double getPercentChange(){return percentChange;}
	public void setPercentChange(Double p){percentChange = p;}
}
