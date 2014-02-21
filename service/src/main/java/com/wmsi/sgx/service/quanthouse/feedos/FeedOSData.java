package com.wmsi.sgx.service.quanthouse.feedos;

import java.util.Date;

public class FeedOSData{

	private Double lastPrice;
	public Double getLastPrice(){return lastPrice;}
	public void setLastPrice(Double l){lastPrice = l;}
	
	private Double openPrice;
	public Double getOpenPrice(){return openPrice;	}
	public void setOpenPrice(Double p){openPrice = p;}
	
	private Double closePrice;
	public Double getClosePrice(){return closePrice;	}
	public void setClosePrice(Double p){closePrice = p;}
	
	private Date currentBusinessDay;	
	public Date getCurrentBusinessDay(){return currentBusinessDay;	}
	public void setCurrentBusinessDay(Date d){currentBusinessDay = d;}
	
	private Date previousBusinessDay;
	public Date getPreviousBusinessDay(){return previousBusinessDay;}
	public void setPreviousBusinessDay(Date d){previousBusinessDay = d;}	
}
