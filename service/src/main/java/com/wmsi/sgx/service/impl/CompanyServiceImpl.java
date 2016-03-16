package com.wmsi.sgx.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.CompanyNameAndTicker;
import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.DividendValue;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.GovTransparencyIndex;
import com.wmsi.sgx.model.GovTransparencyIndexes;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holder;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.IsCompanyNonPremiumModel;
import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.charts.BalanceSheet;
import com.wmsi.sgx.model.charts.CashFlow;
import com.wmsi.sgx.model.charts.GrowthOverPriorYear;
import com.wmsi.sgx.model.charts.IncomeStatement;
import com.wmsi.sgx.model.charts.Ratio;
import com.wmsi.sgx.model.search.ChartDomain;
import com.wmsi.sgx.model.search.ChartRequestModel;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.search.SearchResult;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.query.AlphaFactorIdQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.CompanyNameAndTickerQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.DividendValueQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.EstimatesQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.FinancialsQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.HistoricalValueQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.IsCompanyPremiumQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.RelatedCompaniesQueryBuilder;

@Service
public class CompanyServiceImpl implements CompanyService{
	private static final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

	@Autowired
	private SearchService companySearch;
	
	@Value("${list.permitted.exchanges}")
	private String permittedExchangesList;

	@Override
	@Cacheable(value = "company")
	public Company getById(String id, String currency) throws CompanyServiceException {
		try{
			
			return companySearch.getById(id, Company.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Could not load company by id: "+id,e);
		}
	}
	
	@Autowired
	private SearchService previousCompany;
	@Override
	public Company getPreviousById(String id) throws CompanyServiceException {
		try{
			return previousCompany.getById(id, Company.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Could not load company by id", e);
		}
	}

	@Autowired
	private SearchService keyDevsSearch;
	
	@Override
	@Cacheable(value = "keyDevs")
	public KeyDevs loadKeyDevs(String id) throws CompanyServiceException {
		try{
			return keyDevsSearch.getById(id, KeyDevs.class);
		}
		catch(SearchServiceException e){
			log.error("Exception loading key devs: {}", id);
			KeyDevs ret = new KeyDevs();
			ret.setTickerCode(id);
			ret.setKeyDevs(new ArrayList<KeyDev>());
			return ret;
			//throw new CompanyServiceException("Exception loading key devs", e);
		}
	}

	@Autowired
	private SearchService holdersSearch;

	@Override
	@Cacheable(value = "holders")
	public Holders loadHolders(String id) throws CompanyServiceException {
		try{
			return holdersSearch.getById(id, Holders.class);
		}
		catch(SearchServiceException e){
			log.error("Exception loading holders: {}", id);
			Holders ret = new Holders();
			ret.setTickerCode(id);
			ret.setHolders(new ArrayList<Holder>());
			return ret;
			//throw new CompanyServiceException("Exception loading key devs", e);
		}
	}

	@Autowired
	private SearchService financialSearch;
	
	@Override
	@Cacheable(value = "financials")
	public List<Financial> loadFinancials(String id,String currency) throws CompanyServiceException {
		try{
			return financialSearch.search(new FinancialsQueryBuilder(id), Financial.class).getHits();
		}
		catch(SearchServiceException e){
			log.error("Exception loading financials: {}", id);
			List<Financial> ret = new ArrayList<Financial>();
			return ret;
			//throw new CompanyServiceException("Exception loading financials", e);
		}
	}
	
	
	@Autowired
	private SearchService gtiSearch;
	
	@Override
	@Cacheable(value = "gtis")
	public GovTransparencyIndexes loadGtis(String id) throws CompanyServiceException {
		try{
			return gtiSearch.getById(id, GovTransparencyIndexes.class);
		}
		catch(SearchServiceException e){
			log.error("Exception loading gti: {}", id);
			GovTransparencyIndexes ret = new GovTransparencyIndexes();
			ret.setTickerCode(id);
			ret.setGtis(new ArrayList<GovTransparencyIndex>());
			return ret;
			//throw new CompanyServiceException("Exception loading key devs", e);
		}
	}	

	@Autowired
	private SearchService priceHistorySearch;

	@Override
	@Cacheable(value = "priceHistory")
	public List<HistoricalValue> loadPriceHistory(String id,String currency) throws CompanyServiceException {
		try{
			HistoricalValueQueryBuilder query = new HistoricalValueQueryBuilder(id);
			return priceHistorySearch.search(query, HistoricalValue.class).getHits();
		}
		catch(SearchServiceException e){
			log.error("Exception loading closed price history: {}", id);
			return new ArrayList<HistoricalValue>();
			//throw new CompanyServiceException("Exception loading closed price history", e);
		}
	}
	
	@Autowired
	private SearchService highPriceHistorySearch;

	@Override
	@Cacheable(value = "highPriceHistory")
	public List<HistoricalValue> loadHighPriceHistory(String id,String currency) throws CompanyServiceException {
		try{
			HistoricalValueQueryBuilder query = new HistoricalValueQueryBuilder(id);
			return highPriceHistorySearch.search(query, HistoricalValue.class).getHits();
		}
		catch(SearchServiceException e){
			log.error("Exception loading high price history: {}", id);
			return new ArrayList<HistoricalValue>();
			//throw new CompanyServiceException("Exception loading high price history", e);
		}
	}
	
	@Autowired
	private SearchService lowPriceHistorySearch;

	@Override
	@Cacheable(value = "lowPriceHistory")
	public List<HistoricalValue> loadLowPriceHistory(String id,String currency) throws CompanyServiceException {
		try{
			HistoricalValueQueryBuilder query = new HistoricalValueQueryBuilder(id);
			return lowPriceHistorySearch.search(query, HistoricalValue.class).getHits();
		}
		catch(SearchServiceException e){
			log.error("Exception loading low price history: {}", id);
			return new ArrayList<HistoricalValue>();
			//throw new CompanyServiceException("Exception loading low price history", e);
		}
	}
	
	@Autowired
	private SearchService openPriceHistorySearch;

	@Override
	@Cacheable(value = "openPriceHistory")
	public List<HistoricalValue> loadOpenPriceHistory(String id,String currency) throws CompanyServiceException {
		try{
			HistoricalValueQueryBuilder query = new HistoricalValueQueryBuilder(id);
			return openPriceHistorySearch.search(query, HistoricalValue.class).getHits();
		}
		catch(SearchServiceException e){
			log.error("Exception loading open price history: {}", id);
			return new ArrayList<HistoricalValue>();
			//throw new CompanyServiceException("Exception loading open price history", e);
		}
	}
	

	@Autowired
	private SearchService volumeHistorySearch;

	@Override
	@Cacheable(value = "volumeHistory")
	public List<HistoricalValue> loadVolumeHistory(String id,String currency) throws CompanyServiceException {
		try{
			HistoricalValueQueryBuilder query = new HistoricalValueQueryBuilder(id);
			return volumeHistorySearch.search(query, HistoricalValue.class).getHits();
		}
		catch(SearchServiceException e){
			log.error("Exception loading volume history: {}", id);
			return new ArrayList<HistoricalValue>();
			//throw new CompanyServiceException("Exception loading volume history", e);
		}
	}

	@Autowired
	private SearchService alphaFactorSearch;
	
	@Override
	@Cacheable(value = "alphaFactor")
	public AlphaFactor loadAlphaFactors(String id,String currency) throws CompanyServiceException {

		List<AlphaFactor> hits = null;
		Company info = getById(id, "SGD");

		if(info != null && !StringUtils.isEmpty(info.getGvKey())){
			try{
				AlphaFactorIdQueryBuilder query = new AlphaFactorIdQueryBuilder(info.getGvKey());				
				SearchResult<AlphaFactor> results = alphaFactorSearch.search(query, AlphaFactor.class);
				hits = results.getHits();
			}
			catch(SearchServiceException e){
				log.error("Exception loading alpha factors: {}", id);
				//throw new CompanyServiceException("Exception loading alpha factors", e);
			}
		}

		return hits != null && hits.size() > 0 ? hits.get(0) : null;
	}
	
	@Autowired
	private SearchService dividendHistorySearch;
	@Override
	@Cacheable(value = "dividendHistory")
	public DividendHistory loadDividendHistory(String id,String currency) throws CompanyServiceException {
		try{
			DividendValueQueryBuilder query = new DividendValueQueryBuilder(id);
			return dividendHistorySearch.getById(id, DividendHistory.class);
		}
		catch(SearchServiceException e){
			log.error("Exception loading dividend history: {}", id);
			DividendHistory ret = new DividendHistory();
			ret.setTickerCode(id);
			ret.setDividendValues(new ArrayList<DividendValue>());
			return ret;
			//throw new CompanyServiceException("Exception loading dividend history", e);
		}
	}

	@Override
	@Cacheable(value = "relatedCompanies")
	public List<Company> loadRelatedCompanies(String id, AccountType accType,String currency ) throws CompanyServiceException{
		List<Company> companies = null;
		
		try{
			Company company = getById(id, "sgd");
			RelatedCompaniesQueryBuilder query = new RelatedCompaniesQueryBuilder(company, accType);
			SearchResult<Company> results = companySearch.search(query, Company.class);
			
			if(results != null)
				companies =  results.getHits();
		}
		catch(SearchServiceException e){
			log.error("Could not load related companies: {}", id);
			companies = new ArrayList<Company>();
			//throw new CompanyServiceException("Could not load related companies", e);
		}		
		
		return companies;
	}
	
	@Autowired
	private SearchService estimatesSerach;
	
	@Override
	@Cacheable(value = "estimate")
	public List<Estimate> loadEstimates(String id,String currency) throws CompanyServiceException {
		try{
			return estimatesSerach.search(new EstimatesQueryBuilder(id), Estimate.class).getHits();
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Exception loading estimates", e);
		}
	}
	
	@Autowired
	private SearchService previousEstimate;
	
	@Override
	public List<Estimate> loadPreviousEstimates(String id) throws CompanyServiceException {
		try{
			return previousEstimate.search(new EstimatesQueryBuilder(id), Estimate.class).getHits();
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Exception loading estimates", e);
		}
	}
	
	@Autowired
	private SearchService companyNameAndTickerSearch;
	
	@Override
	public List<CompanyNameAndTicker> loadCompanyNamesAndTickers() throws SearchServiceException{
		
		try{
			return companyNameAndTickerSearch.search(new CompanyNameAndTickerQueryBuilder(), CompanyNameAndTicker.class).getHits();
		}
		catch(SearchServiceException e){
			throw new SearchServiceException("Exception searching CompanyNamesAndTickers", e);
		}
	}
	@Autowired
	private SearchService isCompanyNonPremium;
	
	@Override
	public Boolean isCompanyNonPremium(String tickerCode) throws SearchServiceException{
		try{
			List<IsCompanyNonPremiumModel> list = isCompanyNonPremium.search(new IsCompanyPremiumQueryBuilder(tickerCode), IsCompanyNonPremiumModel.class).getHits();
			List<String> exchangeWhiteList = new ArrayList<>();
			if(permittedExchangesList != null && permittedExchangesList.length() > 0){
				for(int i=0; i<permittedExchangesList.split(",").length; i++){
					exchangeWhiteList.add(permittedExchangesList.split(",")[i]);
				}
			}
			for(IsCompanyNonPremiumModel ispm : list){
				if(exchangeWhiteList.contains(ispm.getExchange())){
					return true;
				}
			}
			return false;
		}
		catch(SearchServiceException e){
			throw new SearchServiceException("Exception searching isCompanyPremium", e);
		}
	}
	
	
	@Override
	public List<?> loadChartData(ChartRequestModel search) throws CompanyServiceException, SearchServiceException {
		
			if(search.getChartDomain().equals(ChartDomain.BALANCE_SHEET)){
				return financialSearch.search(new FinancialsQueryBuilder(search.getId()), BalanceSheet.class).getHits();
			}else{
				if(search.getChartDomain().equals(ChartDomain.CASH_FLOWS)){
					return financialSearch.search(new FinancialsQueryBuilder(search.getId()), CashFlow.class).getHits();
				}else{
					if(search.getChartDomain().equals(ChartDomain.GROWTH_OVER_PRIOR_YEAR)){
						return financialSearch.search(new FinancialsQueryBuilder(search.getId()), GrowthOverPriorYear.class).getHits();
					}else{
						if(search.getChartDomain().equals(ChartDomain.INCOME_STATEMENT)){
							return financialSearch.search(new FinancialsQueryBuilder(search.getId()), IncomeStatement.class).getHits();
						}else{
							if(search.getChartDomain().equals(ChartDomain.RATIOS)){
								return financialSearch.search(new FinancialsQueryBuilder(search.getId()), Ratio.class).getHits();
							}else{
								throw new SearchServiceException("Exception loading ChartingData");
							}}}}}	
	}
}
