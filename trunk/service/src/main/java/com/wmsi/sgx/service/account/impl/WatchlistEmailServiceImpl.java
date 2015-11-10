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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.AlertOption;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.repository.AccountRepository;
import com.wmsi.sgx.repository.WatchlistCompanyRepository;
import com.wmsi.sgx.repository.WatchlistOptionRepository;
import com.wmsi.sgx.repository.WatchlistRepository;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.EmailService;
import com.wmsi.sgx.service.WatchlistSenderService;
import com.wmsi.sgx.service.account.QuanthouseService;
import com.wmsi.sgx.service.account.QuanthouseServiceException;
import com.wmsi.sgx.service.account.WatchlistEmailService;
import com.wmsi.sgx.service.account.WatchlistService;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.util.DateUtil;
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
	private SearchService previousEstimate;
	
	@Autowired
	private SearchService estimatesSerach;
	
	@Override
	@Scheduled(cron="0 20 17 ? * *")
	public void getWatchlistEmails() throws QuanthouseServiceException, CompanyServiceException, SearchServiceException, MessagingException{
		List<Account> accounts = accountRepository.findAll();
		
		for( Account acc: accounts)	{
			if(acc.getActive() == true && acc.getType().equals(AccountType.PREMIUM)){
				List<WatchlistModel> list =	watchlistService.getWatchlist(acc.getUser());
				if(list.size() > 0)
					for(WatchlistModel watchlist : list){
						
						senderService.send(acc.getUser().getUsername(), "SGX StockFacts Premium Alert", parseWatchlist(watchlist, acc), watchlist, quanthouseService.getCompanyPrice(watchlist.getCompanies(), true));
					}
			}
		}
	}
	
	public List<AlertOption> parseWatchlist(WatchlistModel watchlist, Account acct) throws QuanthouseServiceException, CompanyServiceException, SearchServiceException{
		Map<String, Object> map = watchlist.getOptionList();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String todaysDate = sdf.format(new Date());
		String mkt = "XSES";
		
		Map<String, String> priceOptions = new HashMap<String, String>();
		Map<String, String> volumeOptions = new HashMap<String, String>();
		Map<String, String> weekOptions = new HashMap<String, String>();
		Map<String, String> targetPriceOptions = new HashMap<String, String>();
		Map<String, String> consensusRecOptions = new HashMap<String, String>();	
		Map<String, String> keyDevOptions = new HashMap<String, String>();
		
		for(String company : watchlist.getCompanies()){
			String companyName = getCompanyName(company);
			Company comp = companyService.getById(company);
			Price qh = quanthouseService.getPrice(mkt, company);
			List<Estimate> estimates = companyService.loadEstimates(company);
			List<Estimate> pastEstimates = companyService.loadEstimates(company);			
			Estimate currentEstimate = getEstimate(estimates);
			Estimate pastEstimate = getEstimate(pastEstimates);	
			
			if(map.get("pcPriceDrop").toString().equals("true")){	
				String priceDrop = map.get("pcPriceDropBelow").toString();
				String priceRise = map.get("pcPriceRiseAbove").toString();				
				Double priceChange = qh.getPercentChange();
				
				if(!priceDrop.equals("null") && Double.parseDouble(priceDrop) > priceChange){
					priceOptions.put(company, companyName);
				}else if(!priceRise.equals("null") && Double.parseDouble(priceRise) < priceChange){
					priceOptions.put(company, companyName);
				}
			}
			
			if(map.get("pcTradingVolume").toString().equals("true")){				
				
				Double volume = getLastMonthsVolume(companyService.loadVolumeHistory(company), todaysDate);
				if((Double.parseDouble(map.get("pcTradingVolumeValue").toString()) * 0.1 + 1.0) * volume < qh.getLastTradeVolume()){
					volumeOptions.put(company, companyName);
				}
			}
			
			if(map.get("pcReachesWeek").toString().equals("true")){
				String weekValue = map.get("pcReachesWeekValue").toString();
				Double closePrice = qh.getClosePrice();
				if((weekValue.equals("high") && comp.getPriceVs52WeekHigh() < closePrice) || (weekValue.equals("low") && comp.getPriceVs52WeekLow() > closePrice))
					weekOptions.put(company, companyName);
			}
	
			if(map.get("estChangePriceDrop") == null)
				map.put("estChangePriceDrop", "false");
			
			if(map.get("estChangePriceDrop").toString().equals("true") && currentEstimate != null){
				Double targetPriceDrop = Double.parseDouble(map.get("estChangePriceDropBelow").toString());
				Double targetPriceRise = Double.parseDouble(map.get("estChangePriceDropAbove").toString());				
				Double pastTp = pastEstimate.getTargetPrice();
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
			
			
			if(map.get("estChangeConsensus").toString().equals("true") && currentEstimate.getAvgBrokerRec() != null){
				Double brokerRec =  pastEstimate.getAvgBrokerRec();
				Double lastBrokerRec = pastEstimate.getAvgBrokerRec() != null ? pastEstimate.getAvgBrokerRec() : 0.0;				
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
			
			keyDevOptions = sortKeyDevs(getKeyDevs(company, new Date()), map, companyName, company);			
		}	
		
		List<AlertOption> alertList = new ArrayList<AlertOption>();
		
		if(priceOptions.size() > 0){
			AlertOption alert = new AlertOption();
			alert.setCompanies(priceOptions);
			alert.setDescription("Price Drops Below " + map.get("pcPriceDropBelow").toString() + "% Or Rises Over " + map.get("pcPriceRiseAbove").toString() + "% Last Close Price");
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
			alert.setDescription("Change in target price " + map.get("estChangePriceDropBelow").toString() + "% or rises above " + map.get("estChangePriceDropAbove").toString());
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
		if(keyDevOptions.size() > 0){
			AlertOption alert = new AlertOption();
			alert.setCompanies(keyDevOptions);
			alert.setDescription("Key Developments");
			alertList.add(alert);
		}
		return alertList;		
	}
	
	public Map<String, String> sortKeyDevs(List<KeyDev> keyDevs, Map<String, Object> map, String companyName, String tickerCode){
		Map<String, String> keyDevOptions = new HashMap<String, String>();
		for(KeyDev kd : keyDevs){
			for(Map.Entry<String, String> option : getKeyDevMap().entrySet()){
				if(map.get(option.getKey()).toString().equals("true") && kd.getType().equalsIgnoreCase(option.getValue())){
					keyDevOptions.put(tickerCode, companyName);
				}
			}
		}
		return keyDevOptions;
	}
	
	public Map<String, String> getKeyDevMap(){
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("kdAnounceCompTransactions", "");
		ret.put("kdCompanyForecasts", "");
		ret.put("kdCorporateStructureRelated", "");
		ret.put("kdCustProdRelated", "");
		ret.put("kdDividensSplits", "");
		ret.put("kdListTradeRelated", "");
		ret.put("kdPotentialRedFlags", "");
		ret.put("kdPotentialTransactions", "");
		ret.put("kdResultsCorpAnnouncements", "");
		
		return ret;
		
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
	
	private Double getLastMonthsVolume(List<HistoricalValue> lastYear, String startDate) {

		String oneMonthsAgo = DateUtil.adjustDate(startDate, Calendar.DAY_OF_MONTH, -30);
		
		Double vol = 0.0;

		for (HistoricalValue val : lastYear) {
			if (val.getDate().compareTo(DateUtil.toDate(oneMonthsAgo)) >= 0)
				vol += val.getValue();
		}

		return avg(vol, lastYear.size(), 4);
	}
	
	private Double avg(Double sum, Integer total, int scale) {
		BigDecimal s = new BigDecimal(sum);
		BigDecimal t = new BigDecimal(total);
		BigDecimal avg = s.divide(t, RoundingMode.HALF_UP);
		return avg.setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}
}
