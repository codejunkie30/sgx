package com.wmsi.sgx.service.account.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.AlertOption;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.KeyDevsMap;
import com.wmsi.sgx.service.WatchlistSenderService;
import com.wmsi.sgx.service.account.QuanthouseService;
import com.wmsi.sgx.service.account.QuanthouseServiceException;
import com.wmsi.sgx.service.account.WatchlistEmailService;
import com.wmsi.sgx.service.account.WatchlistService;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.util.DateUtil;
import com.wmsi.sgx.util.DefaultHashMap;
import com.wmsi.sgx.util.MathUtil;

@Service
public class WatchlistEmailServiceImpl implements WatchlistEmailService{

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private WatchlistSenderService senderService;
	
	@Autowired
	private WatchlistService watchlistService;	
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private QuanthouseService quanthouseService;
	
	@Autowired
	private SearchService estimatesSerach;
	
	@Autowired
	private KeyDevsMap keyDevsMap;
	
	
	@Override
	public void getEmailsForUser(User usr) throws QuanthouseServiceException, CompanyServiceException, SearchServiceException, MessagingException{
		List<Account> accounts = accountRepository.findAll();			
		for( Account acct: accounts){
			if(acct.getActive() == true && acct.getUser().getUsername().equals(usr.getUsername())){
				List<WatchlistModel> list = watchlistService.getWatchlist(usr);
				if(list.size() > 0)
					for(WatchlistModel watchlist : list){
						List<AlertOption> options = parseWatchlist(watchlist, acct);
						if(watchlist.getCompanies().size() > 0 && options.size() > 0)
							senderService.send(acct.getUser().getUsername(), "SGX StockFacts Premium Alert", options, watchlist, quanthouseService.getCompanyPrice(watchlist.getCompanies()));
					}
				break;
			}
		}
	}
	
	@Override
	public List<AlertOption> parseWatchlist(WatchlistModel watchlist, Account acct) throws QuanthouseServiceException, CompanyServiceException, SearchServiceException{
		Map<String, Object> map = new DefaultHashMap<String,Object>("false");
		map.putAll(watchlist.getOptionList());
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String todaysDate = sdf.format(new Date());
		
		Map<String, String> priceOptions = new HashMap<String, String>();
		Map<String, String> volumeOptions = new HashMap<String, String>();
		Map<String, String> weekOptions = new HashMap<String, String>();
		Map<String, String> targetPriceOptions = new HashMap<String, String>();
		Map<String, String> consensusRecOptions = new HashMap<String, String>();
		
		Map<String, Map<String, String>> keyDevOptions = new HashMap<String, Map<String, String>>();
		keyDevOptions.put("kdAnounceCompTransactions", new HashMap<String, String>());
		keyDevOptions.put("kdCompanyForecasts", new HashMap<String, String>());
		keyDevOptions.put("kdCorporateStructureRelated", new HashMap<String, String>());
		keyDevOptions.put("kdCustProdRelated", new HashMap<String, String>());
		keyDevOptions.put("kdDividensSplits", new HashMap<String, String>());
		keyDevOptions.put("kdListTradeRelated", new HashMap<String, String>());
		keyDevOptions.put("kdPotentialRedFlags", new HashMap<String, String>());
		keyDevOptions.put("kdPotentialTransactions", new HashMap<String, String>());
		keyDevOptions.put("kdResultsCorpAnnouncements", new HashMap<String, String>());		
		
		for(String company : watchlist.getCompanies()){
			Company comp = null;
			Company previousComp = null;
			
			try{
				comp = companyService.getById(company);
				previousComp = companyService.getPreviousById(company);
			}catch(CompanyServiceException e){
				break;
			}
			
			String companyName = getCompanyName(company);
			List<Estimate> estimates = companyService.loadEstimates(company);
			List<Estimate> pastEstimates = companyService.loadPreviousEstimates(company);			
			Estimate currentEstimate = getEstimate(estimates);
			Estimate pastEstimate = getEstimate(pastEstimates);	
			
			if(map.get("pcPriceDrop").toString().equals("true")){	
				String priceDrop = map.get("pcPriceDropBelow").toString();
				String priceRise = map.get("pcPriceRiseAbove").toString();
				
				Double priceChange = 0.0D;

				if(comp != null && previousComp != null){
					if(previousComp.getClosePrice() == null && comp.getClosePrice() != null)
						priceChange = 100.0D;
					else if(previousComp.getClosePrice() != null && comp.getClosePrice() != null)
						priceChange = Math.abs(MathUtil.percentChange(previousComp.getClosePrice(), comp.getClosePrice(), 4));
				}
				
				if(!priceDrop.equals("null") && Double.parseDouble(priceDrop) < priceChange){
					priceOptions.put(company, companyName);
				}else if(!priceRise.equals("null") && Double.parseDouble(priceRise) < priceChange){
					priceOptions.put(company, companyName);
				}
			}
			
			if(map.get("pcTradingVolume").toString().equals("true")){				
				Double volume = getLastMonthsVolume(companyService.loadVolumeHistory(company), todaysDate);
				if(volume == 0.0 && comp.getVolume() != null)
					volumeOptions.put(company, companyName);
				else if(volume > 0.0){
					Double priceChange = Math.abs(MathUtil.percentChange(volume, comp.getVolume(), 4));
					if(Math.abs(Double.parseDouble(map.get("pcTradingVolumeValue").toString())) < priceChange){
						volumeOptions.put(company, companyName);
					}
				}
			}
			
			if(map.get("pcReachesWeek").toString().equals("true")){
				String weekValue = map.get("pcReachesWeekValue").toString();
				Double closePrice = comp.getClosePrice();
				if((weekValue.equals("high") && comp.getPriceVs52WeekHigh() < closePrice) || (weekValue.equals("low") && comp.getPriceVs52WeekLow() > closePrice))
					weekOptions.put(company, companyName);
			}
			
			if(map.get("estChangePriceDrop") == null)
				map.put("estChangePriceDrop", "false");
			
			if(map.get("estChangePriceDrop").toString().equals("true") && currentEstimate != null){
				Double targetPriceDrop = Double.parseDouble(map.get("estChangePriceDropBelow").toString());
				Double targetPriceRise = Double.parseDouble(map.get("estChangePriceDropAbove").toString());
				Double pastTp = null;
				if(pastEstimate != null)
					pastTp = pastEstimate.getTargetPrice();
				Double currentTp = currentEstimate.getTargetPrice();
				Double percentChange = 0.0D;
				
				if(pastTp != null && currentTp != null){
					percentChange = MathUtil.percentChange(pastTp, currentTp, 4);
					if(percentChange > targetPriceRise || percentChange > Math.abs(targetPriceDrop))
						targetPriceOptions.put(company, companyName);					
				}
			}
			
			if(map.get("estChangeConsensus") == null || currentEstimate == null)
				map.put("estChangeConsensus", "false");
			
			if(map.get("estChangeConsensus").toString().equals("true") && currentEstimate != null){
				Double brokerRec =  currentEstimate.getAvgBrokerRec() != null ? currentEstimate.getAvgBrokerRec() : 0.0;
				Double lastBrokerRec = pastEstimate != null && pastEstimate.getAvgBrokerRec() != null ? pastEstimate.getAvgBrokerRec() : 0.0;				
				int pastBrokerRec = (int) Math.round(lastBrokerRec);
				int currentBrokerRec = (int) Math.round(brokerRec);
				
				switch(map.get("estChangeConsensusValue").toString()){				
				case "1" : 
					if(pastBrokerRec != 1 && currentBrokerRec == 1)
						consensusRecOptions.put(company, getCompanyName(company));
				case "2" : 
					if(pastBrokerRec != 5 && currentBrokerRec == 5)
						consensusRecOptions.put(company, getCompanyName(company));
				case "3" :
					if(pastBrokerRec != 3 && currentBrokerRec == 3)
						consensusRecOptions.put(company, getCompanyName(company));
				}
			}
			//KeyDevs
			for(String key: keyDevsMap.getMap().keySet()){
				if(map.get(key).toString().equals("true")){
					for(KeyDev kd : getKeyDevs(company, getStartOfDay(new Date()))){
						if(keyDevsMap.getMap().get(key).contains(kd.getType())){
							keyDevOptions.get(key).put(company, companyName);
						}
							
							
					}
				}
			}					
		}	
		
		List<AlertOption> alertList = new ArrayList<AlertOption>();
		
		if(priceOptions.size() > 0){
			AlertOption alert = new AlertOption();
			alert.setCompanies(priceOptions);
			alert.setDescription("Price drops below " + map.get("pcPriceDropBelow").toString() + "% or rises over " + map.get("pcPriceRiseAbove").toString() + "% last close price");
			alertList.add(alert);
		}
		
		if(volumeOptions.size() > 0){
			AlertOption alert = new AlertOption();
			alert.setCompanies(volumeOptions);
			alert.setDescription("Trading volume exceeds 30 day daily change in volume by "+ map.get("pcTradingVolumeValue").toString() + "%");
			alertList.add(alert);
		}
		if(weekOptions.size() > 0){
			AlertOption alert = new AlertOption();
			alert.setCompanies(weekOptions);
			alert.setDescription("Reaches a new 52-week " + map.get("pcReachesWeekValue").toString());
			alertList.add(alert);
		}
		if(targetPriceOptions.size() > 0){
			AlertOption alert = new AlertOption();
			alert.setCompanies(targetPriceOptions);
			alert.setDescription("Change in target price " + map.get("estChangePriceDropBelow").toString() + "% or rises above " + map.get("estChangePriceDropAbove").toString() + "%");
			alertList.add(alert);
		}
		if(consensusRecOptions.size() > 0){
			AlertOption alert = new AlertOption();
			alert.setCompanies(consensusRecOptions);
			String consensusType = "";
			switch(map.get("estChangeConsensusValue").toString()){
			case "1" :
				consensusType = "buy";
			case "2" :
				consensusType = "sell";
			case "3" :
				consensusType = "hold";
			
			}
			alert.setDescription("Change in consensus recommendation " + consensusType);
			alertList.add(alert);
		}
		
		for(Map.Entry<String, Map<String, String>> entry : keyDevOptions.entrySet()){
			if(entry.getValue().size() > 0){
				AlertOption alert = new AlertOption();
				alert.setCompanies(entry.getValue());
				alert.setDescription(keyDevsMap.getKeyDevLabel(entry.getKey()));
				alertList.add(alert);
			}
		}
		
		return alertList;		
	}	
	
	public Estimate getEstimate(List<Estimate> estimate){
		Estimate ret = null;
		if(estimate != null)
			for(Estimate est : estimate){
				if(est.getPeriod().equals(""))
					ret = est;
			}		
		
		return ret;
	}
	
	public List<KeyDev> getKeyDevs(String company, Date date) throws CompanyServiceException{
		List<KeyDev> ret = new ArrayList<KeyDev>();
		List<KeyDev> kd = companyService.loadKeyDevs(company).getKeyDevs();
		for(KeyDev keyDev : kd){
			if(date.compareTo(keyDev.getDate()) < 1)
				ret.add(keyDev);
		}
		return ret;
		
	}
	
	public String getCompanyName(String company) throws CompanyServiceException{
		return companyService.getById(company).getCompanyName();
	}
	
	public Date getStartOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}
	
	private Double getLastMonthsVolume(List<HistoricalValue> lastYear, String startDate) {
		String oneMonthsAgo = DateUtil.adjustDate(startDate, Calendar.DAY_OF_MONTH, -30);
		
		Double vol = 0.0;

		for (HistoricalValue val : lastYear) {
			if (val.getDate().compareTo(DateUtil.toDate(oneMonthsAgo)) >= 0){
				vol += val.getValue();}
		}

		if(lastYear.size() == 0)
			return vol;
		
		return avg(vol, lastYear.size(), 4);
	}
	
	private Double avg(Double sum, Integer total, int scale) {
		BigDecimal s = new BigDecimal(sum);
		BigDecimal t = new BigDecimal(total);
		BigDecimal avg = s.divide(t, RoundingMode.HALF_UP);
		return avg.setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}
}
