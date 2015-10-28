package com.wmsi.sgx.service.account.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.Watchlist;
import com.wmsi.sgx.domain.WatchlistCompany;
import com.wmsi.sgx.domain.WatchlistOption;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.repository.WatchlistCompanyRepository;
import com.wmsi.sgx.repository.WatchlistOptionRepository;
import com.wmsi.sgx.repository.WatchlistRepository;
import com.wmsi.sgx.service.account.WatchlistService;

@Service
public class WatchlistServiceImpl implements WatchlistService {

	@Autowired
	private WatchlistRepository watchlistRepository;
	
	@Autowired 
	private WatchlistOptionRepository optionRepository;
	
	@Autowired
	private WatchlistCompanyRepository companyRepository;
	
	@Override
	public void renameWatchlist(User user, String watchlistName, String id){
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		for (Watchlist list : watchlist) {
			if(list.getWatchlist_id().equals(Long.parseLong(id))){
				Watchlist update = watchlistRepository.findOne(list.getWatchlist_id());
				update.setName(watchlistName);
				watchlistRepository.save(update);
			}
		}		
	}
	
	@Override
	public List<WatchlistModel> createWatchlist(User user, String watchlistName) {
		
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		if(watchlist.length < 10){
		
			Watchlist newWatchlist = new Watchlist();
			newWatchlist.setUser(user);
			newWatchlist.setDate_created(new Date());
			newWatchlist.setName(watchlistName);
			watchlistRepository.save(newWatchlist);
			
			setOptions(getDefaultOptions(), newWatchlist.getWatchlist_id());
		}
		
		return getWatchlist(user);
	}

	@Override
	public void deleteWatchlist(User user, String id) {
		Long longId = Long.parseLong(id);
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		List<WatchlistCompany> companies = Arrays.asList(companyRepository.findById(longId));
		List<WatchlistOption> options = Arrays.asList(optionRepository.findById(longId));
		
		for (Watchlist list : watchlist) {
			if(list.getWatchlist_id().equals(longId)){
				watchlistRepository.delete(list);
				companyRepository.delete(companies);
				optionRepository.delete(options);
			}
		}
	}
	
	@Override
	public List<WatchlistModel> getWatchlist(User user){
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		
		List<WatchlistModel> ret = new ArrayList<WatchlistModel>();
		
		for(Watchlist list : watchlist){
			Long id = list.getWatchlist_id();
			WatchlistOption[] options = optionRepository.findById(id);
			WatchlistCompany[] companies = companyRepository.findById(id);	
			
			Map<String, String> optionsMap = new HashMap<String, String>();			
			for(WatchlistOption opt : options)
				optionsMap.put(opt.getAlert_option(), opt.getOption_value());			
			
			List<String> companyList = new ArrayList<String>();
			for(WatchlistCompany comp : companies)
				companyList.add(comp.tickerCode);
			
			WatchlistModel model = new WatchlistModel();
			model.setId(list.getWatchlist_id().toString());
			model.setName(list.getName());
			model.setOptionList(optionsMap);
			model.setCompanies(companyList);
			ret.add(model);
		}
		
		return ret;
	}
	
	@Override
	@Transactional
	public void editWatchlist(User user, WatchlistModel model){
		Long id = Long.parseLong(model.getId());
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		for(Watchlist list : watchlist){
			if(list.getWatchlist_id().equals(id)){
				List<WatchlistOption> oldOptions = Arrays.asList(optionRepository.findById(id));
				List<WatchlistCompany> oldCompanies = Arrays.asList(companyRepository.findById(id));
				
				setOptions(model.getOptionList(), id);
				if(model.getCompanies().size() <= 10){
					setCompanies(model.getCompanies(), id);
					companyRepository.delete(oldCompanies);
				}
				
				optionRepository.delete(oldOptions);
				
				break;
			}
		}
			
		
	}
	
	public void setOptions(Map<String, String> map, Long id){
		for(Map.Entry<String, String> entry : map.entrySet()){
			WatchlistOption newOptions = new WatchlistOption();
			newOptions.setAlert_option(entry.getKey());
			newOptions.setOption_value(entry.getValue());
			newOptions.setWatchlistId(id);
			optionRepository.save(newOptions);
		}
	}
	
	public void setCompanies(List<String> companies, Long id){
		for(String comp : companies){
			WatchlistCompany newComp = new WatchlistCompany();
			newComp.setId(id);
			newComp.setTickerCode(comp);
			newComp.setWatchlistId(id);
			companyRepository.save(newComp);
		}
	}
	
	public Map<String, String> getDefaultOptions(){
		Map<String, String> defaultOptions = new HashMap<String, String>();
		defaultOptions.put("pcPriceDrop", "false");
		defaultOptions.put("pcPriceDropBelow", "null");
		defaultOptions.put("pcPriceRiseAbove", "null");
		defaultOptions.put("pcTradingVolume", "false");
		defaultOptions.put("pcTradingVolumeValue", "null");
		defaultOptions.put("pcReachesWeek", "1");
		defaultOptions.put("pcReachesWeekValue", "false");
		defaultOptions.put("estChangePriceDrop", "null");
		defaultOptions.put("estChangePriceDropBelow", "null");
		defaultOptions.put("estChangePriceDropAbove", "null");
		defaultOptions.put("estChangeConsensus", "false");
		defaultOptions.put("estChangeConsensusValue", "1");
		defaultOptions.put("estChangeEstimates", "false");
		defaultOptions.put("estChangeEstimatesValue", "1");
		defaultOptions.put("kdAnounceCompTransactions", "false");
		defaultOptions.put("kdTransactionUpdates", "false");
		defaultOptions.put("kdBankruptcyUpdates", "false");
		defaultOptions.put("kdCompanyForecasts", "false");
		defaultOptions.put("kdCorporateStructureRelated", "false");
		defaultOptions.put("kdCustProdRelated", "false");
		defaultOptions.put("kdDividensSplits", "false");
		defaultOptions.put("kdListTradeRelated", "false");
		defaultOptions.put("kdPotentialRedFlags", "false");
		defaultOptions.put("kdPotentialTransactions", "false");
		defaultOptions.put("kdResultsCorpAnnouncements", "false");
		
		return defaultOptions;
		
	}
	
}
