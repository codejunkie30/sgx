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

import com.wmsi.sgx.domain.TradeEvent;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.model.search.CompanyPrice;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.account.QuanthouseService;
import com.wmsi.sgx.service.account.QuanthouseServiceException;
import com.wmsi.sgx.service.account.TradeEventService;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@Service
public class QuanthouseServiceImpl implements QuanthouseService{

	private static final Logger log = LoggerFactory.getLogger(QuanthouseServiceImpl.class);

	private static final String MARKET_CODE = "XSES";
	private static final String MARKET_EXTENTION = "_RY";

	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private SearchService companySearch;

	@Autowired
	private TradeEventService tradeEventService;

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
	 * @throws SearchServiceException 
	 */
	@Override
	//@Cacheable(value = "price")
	public Price getPrice(String market, String id) throws QuanthouseServiceException, CompanyServiceException, SearchServiceException {
		Date date = new DateTime(new Date()).withTimeAtStartOfDay().toDate();
		List<TradeEvent> event = tradeEventService.getEventsForDate(market, toMarketId(id), date);
		if(event.size() > 0){
			return bindPriceData(event.get(0));
		}
		return fallbackPriceForRealTimePricing(id);				
	}
	
	@Override
	public Price getPriceAt(String market, String id, Date date) throws QuanthouseServiceException, CompanyServiceException, SearchServiceException{
		Date d = new DateTime(date).withTimeAtStartOfDay().toDate();
		
		List<TradeEvent> event = tradeEventService.getEventsForDate(market, toMarketId(id), d);
		if(event.size() > 0){
			for(TradeEvent e : event){
				if(e.getLastTradeTime().compareTo(date) < 0){
					return bindPriceData(e);
				}
			}
		}
		
		return fallbackPriceForRealTimePricing(id);
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
	public List<CompanyPrice> getCompanyPrice(List<String> companies) throws QuanthouseServiceException, CompanyServiceException, SearchServiceException, NumberFormatException{
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
				//Company comp = companyService.getById(company,"sgd");
				Company comp = companyService.getPreviousById(company, "sgd_premium");
				p = fallbackPrice(company);
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
	
	private Price fallbackPriceForRealTimePricing(String id) throws CompanyServiceException, SearchServiceException{
		Price p = new Price();
		Company comp = companySearch.getById(id, Company.class);
		if(comp.getFilingCurrency().toLowerCase() != "sgd"){
			//Its just the confusing name, calling today's company not Previous day, neeed to change the name of method
			Company aseanCompany = companyService.getPreviousById(id, comp.getFilingCurrency().toLowerCase()+"_premium");
			comp = aseanCompany;
		}
		try{
			Company prevComp = companyService.getPreviousById(id, comp.getFilingCurrency().toLowerCase()+"_premium_previous");
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
		p.setTradingCurrency(comp.getFilingCurrency());
		
		return p;
		
	}
	
	private Price fallbackPrice(String id) throws CompanyServiceException, SearchServiceException{
		Price p = new Price();
		Company comp = companySearch.getById(id, Company.class);
		try{
			Company prevComp = companyService.getPreviousById(id, "sgd_premium_previous");
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
		p.setTradingCurrency("SGD");
		
		return p;
		
	}
	private Price bindPriceData(TradeEvent data) {
		Price p = new Price();
		p.setLastPrice(data.getLastPrice());
		p.setClosePrice(data.getClosePrice());
		p.setOpenPrice(data.getOpenPrice());
		p.setCurrentDate(data.getCurrentDate());
		p.setPreviousDate(data.getPreviousDate());
		p.setLastTradeTimestamp(data.getLastTradeTime());
		p.setLastTradeVolume(data.getLastTradeVolume());
		p.setTradingCurrency(data.getTradingCurrency());
		p.setVolume(data.getVolume());
		p.setAskPrice(data.getAskPrice());
		p.setBidPrice(data.getBidPrice());
		p.setHighPrice(data.getHighPrice());
		p.setLowPrice(data.getLowPrice());

		return p;
	}

	@Override
	public void setCurrency(String currency) {
		// TODO Auto-generated method stub
		
	}

}
