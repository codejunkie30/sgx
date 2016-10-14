package com.wmsi.sgx.service.account.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.Watchlist;
import com.wmsi.sgx.domain.WatchlistCompany;
import com.wmsi.sgx.domain.WatchlistOption;
import com.wmsi.sgx.domain.WatchlistTransaction;
import com.wmsi.sgx.model.CompanyWatchlistTransactionHistoryModel;
import com.wmsi.sgx.model.CompanyWatchlistTransactionModel;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.model.WatchlistTransactionModel;
import com.wmsi.sgx.repository.WatchlistCompanyRepository;
import com.wmsi.sgx.repository.WatchlistOptionRepository;
import com.wmsi.sgx.repository.WatchlistRepository;
import com.wmsi.sgx.repository.WatchlistTransactionRepository;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.account.WatchlistService;
import com.wmsi.sgx.util.DateUtil;

/**
 * The WatchlistService handles operations on User WatchList like edit, delete, rename WatchList names
 * 
 */
@Service
public class WatchlistServiceImpl implements WatchlistService {

	@Autowired
	private WatchlistRepository watchlistRepository;
	
	@Autowired 
	private WatchlistOptionRepository optionRepository;
	
	@Autowired
	private WatchlistCompanyRepository companyRepository;
	
	@Autowired
	private WatchlistTransactionRepository transactionRepository;
	
	@Autowired
	private CompanyService companyService;
	
	//Alerts does not have currency conversion feature default currency is used here
	public String defaultCurrency="sgd";
	
	/**
	 * Renames the Watch list name and update with the current date.
	 * 
	 * @param user
	 *            User
	 * @param watchlistName
	 *            String
	 * @param id
	 *            String
	 */
	@Override
	public void renameWatchlist(User user, String watchlistName, String id){
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		for (Watchlist list : watchlist) {
			if(list.getWatchlist_id().equals(Long.parseLong(id))){
				Watchlist update = watchlistRepository.findOne(list.getWatchlist_id());
				update.setName(watchlistName);
				update.setUpdatedDate(new Date());
				watchlistRepository.save(update);
			}
		}		
	}
	
	/**
	 * Creates the Watch list name and update with the current date.
	 * 
	 * @param user
	 *            User
	 * @param watchlistName
	 *            String
	 * @return List of WatchlistModel
	 */
	@Override
	public List<WatchlistModel> createWatchlist(User user, String watchlistName) {
		
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		if(watchlist.length <= 10){
		
			Watchlist newWatchlist = new Watchlist();
			newWatchlist.setUser(user);
			newWatchlist.setDate_created(new Date());
			newWatchlist.setName(watchlistName);
			newWatchlist.setUpdatedDate(new Date());
			watchlistRepository.save(newWatchlist);
			
			setOptions(getDefaultOptions(), newWatchlist.getWatchlist_id());
		}
		
		return getWatchlist(user);
	}
	
	/**
	 * Deletes the watch list along with the companies and related transactions.
	 * 
	 * @param user User
	 * @param id String
	 */
	@Override
	public void deleteWatchlist(User user, String id) {
		Long longId = Long.parseLong(id);
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		List<WatchlistCompany> companies = Arrays.asList(companyRepository.findById(longId));
		List<WatchlistOption> options = Arrays.asList(optionRepository.findById(longId));
		List<WatchlistTransaction> transactions = Arrays.asList(transactionRepository.findById(longId));
		
		for (Watchlist list : watchlist) {
			if(list.getWatchlist_id().equals(longId)){
				watchlistRepository.delete(list);
				companyRepository.delete(companies);
				optionRepository.delete(options);
				transactionRepository.delete(transactions);
			}
		}
	}
	
	/**
	 * Deletes the watchlist along with the companies and related transactions.
	 * 
	 * @param user
	 *            User
	 * @return List
	 */
	@Override
	@Transactional
	public List<String> cleanWatchlist(User user){
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		List<String> ret = new ArrayList<String>();
		
		for(Watchlist list : watchlist){
			Long id = list.getWatchlist_id();
			WatchlistCompany[] companies = companyRepository.findById(id);
			List<String> existingCompanies = new ArrayList<String>();
			for(WatchlistCompany comp : companies){
				try{
					companyService.getCompanyByIdAndIndex(comp.getTickerCode(), "sgd_premium");
					existingCompanies.add(comp.getTickerCode());
				}catch(CompanyServiceException e){
					ret.add(comp.getTickerCode());
				}
				
				
				}
			if(companies.length != existingCompanies.size()){
				addCompanies(user, id.toString(), existingCompanies);
				deleteMissingCompaniesTransactions(id,ret);
			}
			
		}
		return ret;
		
	}
	
	/**
	 * Retrievee the watchlists.
	 * 
	 * @param id
	 *            Long, ret List.
	 * 
	 */
	
	private void deleteMissingCompaniesTransactions(Long id, List<String> ret) {
		for (String tickerCode : ret) {
			WatchlistTransaction[] deleteCompanyTransactions = transactionRepository.findByTickerCode(id, tickerCode);
			if (deleteCompanyTransactions.length >= 1) {
				transactionRepository.delete(Arrays.asList(deleteCompanyTransactions));
			}
		}
	}
	
	/**
	 * Retrieves the watch lists.
	 * 
	 * @param user
	 *            User
	 * @return List of WatchlistModel
	 */
	@Override
	public List<WatchlistModel> getWatchlist(User user){
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		
		List<WatchlistModel> ret = new ArrayList<WatchlistModel>();
		
		for(Watchlist list : watchlist){
			Long id = list.getWatchlist_id();
			WatchlistOption[] options = optionRepository.findById(id);
			WatchlistCompany[] companies = companyRepository.findById(id);	
			
			Map<String, Object> optionsMap = new HashMap<String, Object>();			
			for(WatchlistOption opt : options){
				String optValue = opt.getOption_value();
				if(optValue.equalsIgnoreCase("false") | optValue.equalsIgnoreCase("true"))
						optionsMap.put(opt.getAlert_option(), Boolean.parseBoolean(optValue));	
				else if(optValue.equalsIgnoreCase("null"))
						optionsMap.put(opt.getAlert_option(), null);	
				else
					optionsMap.put(opt.getAlert_option(), optValue);
			}
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
	
	/**
	 * Edits the Watch list name.
	 * 
	 * @param user
	 *            User
	 * @param model
	 *            WatchlistModel
	 */
	@Override
	@Transactional
	public void editWatchlist(User user, WatchlistModel model){
		Long id = Long.parseLong(model.getId());
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		for(Watchlist list : watchlist){
			if(list.getWatchlist_id().equals(id)){
				List<WatchlistOption> oldOptions = Arrays.asList(optionRepository.findById(id));
				List<WatchlistCompany> oldCompanies = Arrays.asList(companyRepository.findById(id));
				
				if(oldCompanies.size() != model.getCompanies().size()){
					for (WatchlistCompany watchlistCompany : oldCompanies) {
						if (!model.getCompanies().contains(watchlistCompany.getTickerCode())) {
							WatchlistTransaction[] deleteCompanyTransactions = transactionRepository.findByTickerCode(id, watchlistCompany.getTickerCode());
							if (deleteCompanyTransactions.length >= 1) {
								transactionRepository.delete(Arrays.asList(deleteCompanyTransactions));
							}
							break;
						}
					}
				}
				setOptions(model.getOptionList(), id);
				if(model.getCompanies().size() <= 25){
					setCompanies(model.getCompanies(), id);
					companyRepository.delete(oldCompanies);
				}
				
				optionRepository.delete(oldOptions);
				
				break;
			}
		}		
	}
	
	/**
	 * Adds the companies in watchlist.
	 * 
	 * @param user
	 *            User
	 * @param addId
	 *            String
	 * @param companies
	 *            List of companies
	 * @return Response
	 */
	@Override
	@Transactional
	public Response addCompanies(User user, String addId, List<String> companies){
		Long id = Long.parseLong(addId);
		Watchlist[] watchlist = watchlistRepository.findByUser(user);
		Response response = new Response();
		
		for(Watchlist list : watchlist){
			if(list.getWatchlist_id().equals(id)){
				List<WatchlistCompany> oldCompanies = Arrays.asList(companyRepository.findById(id));
				
				if(companies.size() <= 25){
					setCompanies(companies, id);
					companyRepository.delete(oldCompanies);
					response.setMessage("success");
					return response;
				}				
			}
		}
		
		response.setMessage("failure");
		return response;
	}
	
	/**
	 * Adds the watch list transactions.
	 * 
	 * @param user User
	 * @param addId String
	 * @param transactions List of WatchlistTransactionModel
	 * @return Response
	 */
	@Override
	@Transactional
	public Response addTransactions(User user, String addId, List<WatchlistTransactionModel> transactions) {
		Long id = Long.parseLong(addId);
		Response response = new Response();
		if (transactions != null) {
			Watchlist[] watchlist = watchlistRepository.findByUser(user);
			for (Watchlist list : watchlist) {
				if (list.getWatchlist_id().equals(id)) {
					setTransactions(transactions, id, user);
					response.setMessage("success");
					return response;
				}
			}
		}
		response.setMessage("failure");
		return response;
	}
	
	/**
	 * Deletes the transaction for the user's watch list.
	 * 
	 * @param user User
	 * @param id String
	 * @param transactionId String
	 * @return Response
	 */
	@Override
	@Transactional
	public Response deleteTransactions(User user,String id, String transactionId) {
		Long watchlistId = Long.parseLong(id);
		Long transId = Long.parseLong(transactionId);
		Response response = new Response();

		if (id != null && transId != null) {
			Watchlist[] watchlist = watchlistRepository.findByUser(user);
			for (Watchlist list : watchlist) {
				if (list.getWatchlist_id().equals(watchlistId)) {
					WatchlistTransaction transaction = transactionRepository.findByIds(watchlistId, transId);
					transactionRepository.delete(transaction);
					response.setMessage("success");
					return response;
				}
			}
		}
		response.setMessage("failure");
		return response;
	}
	
	/**
	 * Retrieves the transactions.
	 * 
	 * @param user User
	 * @param id String
	 * @return Map
	 */
	@Override
	public Map<String, List<WatchlistTransactionModel>> getTransactions(User user, String id) {
		Long watchlistId = Long.parseLong(id);
		Map<String, List<WatchlistTransactionModel>> transactionMap = new HashMap<>();
		List<WatchlistTransactionModel> transactionModelList;

		if (id != null) {
			Watchlist[] watchlist = watchlistRepository.findByUser(user);

			for (Watchlist list : watchlist) {
				if (list.getWatchlist_id().equals(watchlistId)) {
					WatchlistTransaction[] transactions = transactionRepository.findById(watchlistId);
					for (WatchlistTransaction transaction : transactions) {
						WatchlistTransactionModel transactionModel = new WatchlistTransactionModel();
						BeanUtils.copyProperties(transaction, transactionModel);
						if(transactionMap.get(transaction.getTickerCode()) == null){
							transactionModelList = new ArrayList<>();
							transactionMap.put(transaction.getTickerCode(), transactionModelList);
						}else{
							transactionModelList = transactionMap.get(transaction.getTickerCode());
						}
						
						transactionModelList.add(transactionModel);
					}
					return transactionMap;
				}
			}
		}
		return transactionMap;
	}
	
	/**
	 * Retrieves the transactions for the watch list.
	 * 
	 * @param user User
	 * @param id String
	 * @return CompanyWatchlistTransactionHistoryModel
	 */
	@Override
	public CompanyWatchlistTransactionHistoryModel getWatchListTransactions(User user, String id) {
		Long watchlistId = Long.parseLong(id);
		CompanyWatchlistTransactionHistoryModel companyWatchlistTransactionHistoryModel = new CompanyWatchlistTransactionHistoryModel();
		Map<String, CompanyWatchlistTransactionModel> transactionMap = new HashMap<>();
		companyWatchlistTransactionHistoryModel.setCompanies(transactionMap);
		
		CompanyWatchlistTransactionModel companyWatchlistTransactionModel;
		List<WatchlistTransactionModel> transactionModelList;
		double transactionValue;

		if (id != null) {
			Watchlist[] watchlist = watchlistRepository.findByUser(user);

			for (Watchlist list : watchlist) {
				if (list.getWatchlist_id().equals(watchlistId)) {
					WatchlistTransaction[] transactions = transactionRepository.findById(watchlistId);
					for (WatchlistTransaction transaction : transactions) {
						WatchlistTransactionModel transactionModel = new WatchlistTransactionModel();
						BeanUtils.copyProperties(transaction, transactionModel);
						
						if(transactionMap.get(transaction.getTickerCode()) == null){
							companyWatchlistTransactionModel = new CompanyWatchlistTransactionModel();
							transactionModelList = new ArrayList<>();
							companyWatchlistTransactionModel.setTradeDate(transaction.getTradeDate());
							companyWatchlistTransactionModel.setTransactions(transactionModelList);
							transactionMap.put(transaction.getTickerCode(), companyWatchlistTransactionModel);
						}else{
							companyWatchlistTransactionModel = transactionMap.get(transaction.getTickerCode());
							transactionModelList = companyWatchlistTransactionModel.getTransactions();
						}
						transactionValue = transaction.getNumberOfShares() * transaction.getCostAtPurchase();
						if("BUY".equalsIgnoreCase(transaction.getTransactionType())){
							companyWatchlistTransactionModel.setNumberOfShares(companyWatchlistTransactionModel.getNumberOfShares() + transaction.getNumberOfShares());
							companyWatchlistTransactionModel.setInvestement(companyWatchlistTransactionModel.getInvestement() + transactionValue);
							companyWatchlistTransactionHistoryModel
							.setTotalInvested(companyWatchlistTransactionHistoryModel.getTotalInvested()
									+ transactionValue);
						}else{
							companyWatchlistTransactionModel.setNumberOfShares(companyWatchlistTransactionModel.getNumberOfShares() - transaction.getNumberOfShares());
							companyWatchlistTransactionModel.setInvestement(companyWatchlistTransactionModel.getInvestement() - transactionValue);
							companyWatchlistTransactionHistoryModel
							.setTotalInvested(companyWatchlistTransactionHistoryModel.getTotalInvested()
									- transactionValue);
						}
						transactionModelList.add(transactionModel);
						
					}
					return companyWatchlistTransactionHistoryModel;
				}
			}
		}
		return companyWatchlistTransactionHistoryModel;
	}
	
	/**
	 * Retrieves the stocklist companies.
	 * 
	 * @param transactions
	 *            List, id Long, user User.
	 * 
	 */

	public void setTransactions(List<WatchlistTransactionModel> transactions, Long id, User user) {
		for (WatchlistTransactionModel watchlistTransactionModel : transactions) {
			WatchlistTransaction transaction = new WatchlistTransaction();
			BeanUtils.copyProperties(watchlistTransactionModel,transaction);
			transaction.setTradeDate(DateUtil.resetTimeStamp(transaction.getTradeDate()));
			transaction.setLastModifiedBy(user);
			transaction.setLastModifiedDate(new Date());
			transaction.setCreatedBy(user);
			transaction.setCreatedDate(new Date());
			transaction.setWatchlistId(id);
			transactionRepository.save(transaction);
		}
	}
	
	/**
	 * Sets the options in watch list.
	 * 
	 * @param map
	 * @param id
	 */
	public void setOptions(Map<String, Object> map, Long id){
		for(Map.Entry<String, Object> entry : map.entrySet()){
			WatchlistOption newOptions = new WatchlistOption();
			newOptions.setAlert_option(entry.getKey());
			if(entry.getValue() == null)
				newOptions.setOption_value("null");
			else
				newOptions.setOption_value(entry.getValue().toString());
			newOptions.setWatchlistId(id);
			optionRepository.save(newOptions);
		}
	}
	
	/**
	 * Sets the companies with id,tickercode and watchlistid.
	 * 
	 * @param companies
	 *            List, id Long.
	 * 
	 */
	
	public void setCompanies(List<String> companies, Long id){
		for(String comp : companies){
			WatchlistCompany newComp = new WatchlistCompany();
			newComp.setId(id);
			newComp.setTickerCode(comp);
			newComp.setWatchlistId(id);
			companyRepository.save(newComp);
		}
	}
	
	/**
	 * Retrieves the default option values.
	 * 
	 * @return Map
	 */
	
	public Map<String, Object> getDefaultOptions(){
		Map<String, Object> defaultOptions = new HashMap<String, Object>();
		defaultOptions.put("pcPriceDrop", "false");
		defaultOptions.put("pcPriceDropBelow", "null");
		defaultOptions.put("pcPriceRiseAbove", "null");
		defaultOptions.put("pcTradingVolume", "false");
		defaultOptions.put("pcTradingVolumeValue", "null");
		defaultOptions.put("pcReachesWeek", "false");
		defaultOptions.put("pcReachesWeekValue", "null");
		defaultOptions.put("estChangePriceDrop", "false");
		defaultOptions.put("estChangePriceDropBelow", "null");
		defaultOptions.put("estChangePriceDropAbove", "null");
		defaultOptions.put("estChangeConsensus", "false");
		defaultOptions.put("estChangeConsensusValue", "1");
		defaultOptions.put("kdAnounceCompTransactions", "false");
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
	
	/**
	 * Retrieves the Stock list/Watch list companies.
	 * 
	 * @param id String
	 * @return List Watch/Stock list companies
	 */
	@Override
	public List<WatchlistCompany> getStockListCompanies(String id) {
		WatchlistCompany[] companies = companyRepository.findById(Long.parseLong(id));
		return Arrays.asList(companies);
	}
	
}
