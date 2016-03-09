package com.wmsi.sgx.service.account.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.elasticsearch.common.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.FXConversionMultiplerTable;
import com.wmsi.sgx.domain.TradeEvent;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.model.search.CompanyPrice;
import com.wmsi.sgx.repository.FxConversionRepository;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.account.QuanthouseService;
import com.wmsi.sgx.service.account.QuanthouseServiceException;
import com.wmsi.sgx.service.account.TradeEventService;

@Service
public class QuanthouseServiceImpl implements QuanthouseService{

	private static final Logger log = LoggerFactory.getLogger(QuanthouseServiceImpl.class);

	private static final String MARKET_CODE = "XSES";
	private static final String MARKET_EXTENTION = "_RY";

	@Autowired
	private CompanyService companyService;

	@Autowired
	private TradeEventService tradeEventService;
	
	@Autowired
	private FxConversionRepository fxConversionRepository;
	
	public String currency;
	public String defaultCurrency="sgd";
	public double currencyMultiplier;
	
	@Override
	public void setCurrency(String currency){
		this.currency = currency;
		
		List<FXConversionMultiplerTable> fxConversionMultiplier = fxConversionRepository.findBydateAfter(new DateTime().withTimeAtStartOfDay().minusDays(2).toDate());
		this.currencyMultiplier = findCurrencyMultiplier(fxConversionMultiplier, this.currency);
	}

	/**
	 * Get intraday price data for the given id within the given market
	 * 
	 * @param market
	 *            - Market ID belongs too
	 * @param id
	 *            - Local Market identifier
	 * @return The last price
	 * @throws QuanthouseServiceException
	 * @throws CompanyServiceException 
	 */
	@Override
	//@Cacheable(value = "price")
	public Price getPrice(String market, String id) throws QuanthouseServiceException, CompanyServiceException {
		Date date = new DateTime(new Date()).withTimeAtStartOfDay().toDate();
		List<TradeEvent> event = tradeEventService.getEventsForDate(market, toMarketId(id), date);
		if(event.size() > 0){
			return bindPriceData(event.get(0));
		}
		return fallbackPrice(id);				
	}
	
	private double findCurrencyMultiplier(List<FXConversionMultiplerTable> fxConversionMultiplier, String currency){
		

		switch(currency.toLowerCase()) {
		    case "hkd":
		    	 return fxConversionMultiplier.get(0).getSgd_to_hkd();
		    case "myr":
		    	return fxConversionMultiplier.get(0).getSgd_to_myr();
		    case "usd":
		    	 return fxConversionMultiplier.get(0).getSgd_to_usd();
		    case "idr":
		    	 return fxConversionMultiplier.get(0).getSgd_to_idr();
		    case "php":
		    	 return fxConversionMultiplier.get(0).getSgd_to_php();
		    case "thb":
		    	 return fxConversionMultiplier.get(0).getSgd_to_thb();
		    case "twd":
		    	 return fxConversionMultiplier.get(0).getSgd_to_twd();
		}
		return 1.0;
	}
	
	@Override
	public Price getPriceAt(String market, String id, Date date) throws QuanthouseServiceException, CompanyServiceException{
		Date d = new DateTime(date).withTimeAtStartOfDay().toDate();
		
		List<TradeEvent> event = tradeEventService.getEventsForDate(market, toMarketId(id), d);
		if(event.size() > 0){
			for(TradeEvent e : event){
				if(e.getLastTradeTime().compareTo(date) < 0){
					return bindPriceData(e);
				}
			}
		}
		
		return fallbackPrice(id);
	}

	/**
	 * Get intraday prices for all trade events by day. 
	 * @param market
	 *            - Market ID belongs too
	 * @param id
	 *            - Local Market identifier
	 * @return The prices for
	 * @throws QuanthouseServiceException
	 */
	@Override
	public List<Price> getIntradayPrices(String market, String id) throws QuanthouseServiceException {

		List<Price> ret = new ArrayList<Price>();
		List<TradeEvent> events = tradeEventService.getEventsForDate(market, toMarketId(id), new Date());
		
		for(TradeEvent e : events){
			ret.add(bindPriceData(e));
		}
		
		return ret;
	}
	
	@Override
	public List<Price> getPricingHistory(String market, String id, Date date) throws QuanthouseServiceException {

		List<Price> ret = new ArrayList<Price>();
		List<TradeEvent> events = tradeEventService.getEventsForDate(market, toMarketId(id), date);
		
		Double currentPrice = 0.0;
		for(TradeEvent e : events){
			if(!e.getLastPrice().equals(currentPrice)){
				currentPrice = e.getLastPrice();
				ret.add(bindPriceData(e));
			}
		}
		
		return ret;
	}
	
	@Override
	public List<CompanyPrice> getCompanyPrice(List<String> companies) throws QuanthouseServiceException, CompanyServiceException{
		List<CompanyPrice> list = new ArrayList<CompanyPrice>();
		
		if(companies == null)
			return list;
		
		for(String company : companies){
			CompanyPrice companyPrice = new CompanyPrice();
			companyPrice.setPrice(0.0);
			companyPrice.setChange(0.0);
			companyPrice.setCurrency("SGD");
			Price p = new Price();
			DecimalFormat df = new DecimalFormat("0.###");
			
			try{
				Company comp = companyService.getById(company,defaultCurrency);
				p = fallbackPrice(company);
				// set to default currency as currency conversion does not apply on watchlist
				p.setTradingCurrency(defaultCurrency);
				companyPrice.setChange(Double.valueOf(df.format(p.getChange())));
				companyPrice.setCompanyName(comp.getCompanyName());
				companyPrice.setCurrency(p.getTradingCurrency());
				if(p.getLastPrice() != null)
					companyPrice.setPrice(Double.valueOf(df.format(p.getLastPrice())));
				
			}catch(CompanyServiceException e){
				break;
			}
			companyPrice.setTicker(company);
			list.add(companyPrice);
		}
		
		return list;
		
	}
	
	private String toMarketId(String id){
		return id.concat(MARKET_EXTENTION);
	}
	
	private Price fallbackPrice(String id) throws CompanyServiceException{
		Price p = new Price();
		Company comp = companyService.getById(id,defaultCurrency);
		try{
			Company prevComp = companyService.getPreviousById(id);
			p.setClosePrice(prevComp.getClosePrice());
			p.setPreviousDate(prevComp.getPreviousCloseDate());
		}catch(CompanyServiceException e){
			p.setClosePrice(comp.getClosePrice());
			p.setPreviousDate(comp.getPreviousCloseDate());
		}
		p.setOpenPrice(comp.getOpenPrice());
		p.setLastPrice(comp.getClosePrice());
		p.setCurrentDate(comp.getPreviousCloseDate());		
		p.setLastTradeTimestamp(comp.getPreviousCloseDate());
		p.setLastTradeVolume(comp.getVolume());
		p.setLowPrice(comp.getLowPrice());
		p.setHighPrice(comp.getHighPrice());
		p.setTradingCurrency(this.currency.toUpperCase());
		
		return p;
		
	}

	private Price bindPriceData(TradeEvent data) {
		Price p = new Price();
		if(this.currency != "sgd" || this.currency != "sgd".toUpperCase()){
			p.setLastPrice(data.getLastPrice() * this.currencyMultiplier);
			p.setClosePrice(data.getClosePrice()* this.currencyMultiplier);
			p.setOpenPrice(data.getOpenPrice()* this.currencyMultiplier);
			p.setAskPrice(data.getAskPrice()* this.currencyMultiplier);
			p.setBidPrice(data.getBidPrice()* this.currencyMultiplier);
			p.setHighPrice(data.getHighPrice()* this.currencyMultiplier);
			p.setLowPrice(data.getLowPrice()* this.currencyMultiplier);
			p.setTradingCurrency(this.currency.toUpperCase());
		}else{
			p.setLastPrice(data.getLastPrice());
			p.setClosePrice(data.getClosePrice());
			p.setOpenPrice(data.getOpenPrice());
			p.setAskPrice(data.getAskPrice());
			p.setBidPrice(data.getBidPrice());
			p.setHighPrice(data.getHighPrice());
			p.setLowPrice(data.getLowPrice());
			p.setTradingCurrency(data.getTradingCurrency());
		}
		
		p.setCurrentDate(data.getCurrentDate());
		p.setPreviousDate(data.getPreviousDate());
		p.setLastTradeTimestamp(data.getLastTradeTime());
		p.setLastTradeVolume(data.getLastTradeVolume());
		p.setVolume(data.getVolume());
		return p;
	}
	
}
