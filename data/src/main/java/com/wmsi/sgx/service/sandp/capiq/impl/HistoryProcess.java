package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.Date;
import java.util.List;

import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

public class HistoryProcess {
	
	/**
	 * Process PriceHistory with additional values
	 * @param PriceHistory
	 * @return Updated PriceHistory
	 */
	public PriceHistory processHistory(PriceHistory history){
		List<HistoricalValue> price = history.getPrice();
		List<HistoricalValue> volume = history.getVolume();
		List<HistoricalValue> highPrice = history.getHighPrice();
		List<HistoricalValue> lowPrice = history.getLowPrice();
		List<HistoricalValue> openPrice = history.getOpenPrice();
		//New copies to add to
		List<HistoricalValue> volume2 = history.getVolume();
		List<HistoricalValue> highPrice2 = history.getHighPrice();
		List<HistoricalValue> lowPrice2 = history.getLowPrice();
		List<HistoricalValue> openPrice2 = history.getOpenPrice();
		
		for(HistoricalValue currPrice : price){
			HistoricalValue currVol = matchingDates(currPrice, volume);
			if(currVol != null){
				volume2.add(currVol);
			}
			HistoricalValue currHighPrice = matchingDates(currPrice, highPrice);
			if(currHighPrice != null){
				highPrice2.add(currHighPrice);
			}
			HistoricalValue currLowPrice = matchingDates(currPrice, lowPrice);
			if(currLowPrice != null){
				lowPrice2.add(currLowPrice);				
			}
			HistoricalValue currOpenPrice = matchingDates(currPrice, openPrice);
			if(currOpenPrice != null){
				openPrice2.add(currOpenPrice);
			}
		}
		history.setHighPrice(highPrice2);
		history.setLowPrice(lowPrice2);
		history.setOpenPrice(openPrice2);
		history.setVolume(volume2);
		return history;
	}
	
	public HistoricalValue matchingDates(HistoricalValue value, List<HistoricalValue> list){
		Boolean found = false;
		for(HistoricalValue value2 : list){
			if(value.getDate().equals(value2.getDate())){
				found = true;
				break;
			}
		}
		if(!found){
			return addEntry(value.getTickerCode(), value.getDate());
		}else{
			return null;
		}
	}
	
	public HistoricalValue addEntry(String id, Date date){
		HistoricalValue val = new HistoricalValue();
		val.setTickerCode(id);
		val.setValue(Double.valueOf("0"));
		val.setDate(date);
		return val;
	}
}
