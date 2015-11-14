package com.wmsi.sgx.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.CompanyNameAndTicker;
import com.wmsi.sgx.model.CompanyNameAndTickerList;
import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.GovTransparencyIndexes;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.charts.BalanceSheet;
import com.wmsi.sgx.model.charts.BalanceSheets;
import com.wmsi.sgx.model.charts.CashFlow;
import com.wmsi.sgx.model.charts.CashFlows;
import com.wmsi.sgx.model.charts.GrowthOverPriorYear;
import com.wmsi.sgx.model.charts.GrowthOverPriorYears;
import com.wmsi.sgx.model.charts.IncomeStatement;
import com.wmsi.sgx.model.charts.IncomeStatements;
import com.wmsi.sgx.model.charts.Ratio;
import com.wmsi.sgx.model.charts.Ratios;
import com.wmsi.sgx.model.search.ChartDomain;
import com.wmsi.sgx.model.search.ChartRequestModel;
import com.wmsi.sgx.model.search.IdSearch;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.security.UserDetailsWrapper;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.conversion.ModelMapper;
import com.wmsi.sgx.service.search.SearchServiceException;

@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class CompanyController{

	@Autowired
	private CompanyService companyService;
	
	@Autowired 
	private ModelMapper mapper;
	
	@Autowired
	private AccountService accountService;
	
	@RequestMapping(value="company")
	public Map<String, Object> getAll(@RequestBody IdSearch search) throws CompanyServiceException {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("company", getCompany(search));
		ret.put("holders", getHolders(search));
		ret.put("keyDevs", getKeyDevs(search).getKeyDevs());
		ret.put("alphaFactors", getAlphas(search));
		ret.put("gtis", getGtis(search));
		ret.put("dividendHistory", getDividendHistory(search));
		return ret;
	}
	
	@RequestMapping(value="company/techChart")
	public Map<String, Object> getTechChart(@RequestBody IdSearch search) throws CompanyServiceException {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("company", getCompany(search));
		ret.put("dividendHistory", getDividendHistory(search));
		ret.put("financials", getFinancials(search));
		ret.put("estimates",getEstimates(search));
		return ret;
	}
	
	@RequestMapping(value="company/info")
	public Map<String, Company> getCompany(@RequestBody IdSearch search) throws CompanyServiceException{
		Map<String, Company> ret = new HashMap<String, Company>();
		ret.put("companyInfo", companyService.getById(search.getId()));
		return ret;
	}
	
	@RequestMapping(value="company/keyDevs")
	public KeyDevs getKeyDevs(@RequestBody IdSearch search) throws CompanyServiceException{
		KeyDevs keydevs = companyService.loadKeyDevs(search.getId());
		
		if(keydevs.getKeyDevs() != null && keydevs.getKeyDevs().size() > 10)
			keydevs.setKeyDevs(keydevs.getKeyDevs().subList(keydevs.getKeyDevs().size() - 10, keydevs.getKeyDevs().size()));
		return keydevs;
	}

	@RequestMapping(value="company/holders")
	public Holders getHolders(@RequestBody IdSearch search) throws CompanyServiceException {
		Holders holders = companyService.loadHolders(search.getId());
		
		if(holders.getHolders() != null && holders.getHolders().size() > 5)
			holders.setHolders(holders.getHolders().subList(0,  5)); // Only return first 5 holders
		
		return holders;
	}
	
	@RequestMapping(value="company/financials")
	public Financials getFinancials(@RequestBody IdSearch search) throws CompanyServiceException {		
		List<Financial> hits = companyService.loadFinancials(search.getId());
		Financials ret = new Financials();
		ret.setFinancials(hits);
		return ret;
	}
	
	@RequestMapping(value="company/estimates")
	public Estimates getEstimates(@RequestBody IdSearch search) throws CompanyServiceException {		
		List<Estimate> hits = companyService.loadEstimates(search.getId());
		Estimates ret = new Estimates();
		ret.setEstimates(hits);
		return ret;
	}
	
	@RequestMapping(value="company/names")
	public CompanyNameAndTickerList getCompanyNamesAndTickers() throws SearchServiceException {		
		List<CompanyNameAndTicker> hits = companyService.loadCompanyNamesAndTickers();
		CompanyNameAndTickerList ret = new CompanyNameAndTickerList();
		ret.setCompanyNameAndTickerList(hits);
		return ret;
	}
	
	@RequestMapping(value="company/techCharts/balanceSheets")
	public BalanceSheets getBalanceSheetCharts(@RequestBody IdSearch search) throws CompanyServiceException, SearchServiceException  {	
		ChartRequestModel req = new ChartRequestModel();
		req.setChartDomain(ChartDomain.BALANCE_SHEET);
		req.setId(search.getId());
		@SuppressWarnings("unchecked")
		List<BalanceSheet> hits = (List<BalanceSheet>) companyService.loadChartData(req);
		BalanceSheets ret = new BalanceSheets();
		ret.setBalanceSheet(hits);
		return ret;
	}
	

	@RequestMapping(value="company/techCharts/ratios")
	public Ratios getRatioChartsData(@RequestBody IdSearch search) throws CompanyServiceException, SearchServiceException  {		
		ChartRequestModel req = new ChartRequestModel();
		req.setChartDomain(ChartDomain.RATIOS);
		req.setId(search.getId());
		@SuppressWarnings("unchecked")
		List<Ratio> hits = (List<Ratio>) companyService.loadChartData(req);
		Ratios ret = new Ratios();
		ret.setRatios(hits);
		return ret;
	}
	

	@RequestMapping(value="company/techCharts/income")
	public IncomeStatements getIncomeChartsData(@RequestBody IdSearch search) throws CompanyServiceException, SearchServiceException  {		
		ChartRequestModel req = new ChartRequestModel();
		req.setChartDomain(ChartDomain.INCOME_STATEMENT);
		req.setId(search.getId());
		@SuppressWarnings("unchecked")
		List<IncomeStatement> hits = (List<IncomeStatement>) companyService.loadChartData(req);
		IncomeStatements ret = new IncomeStatements();
		ret.setIncomeStatements(hits);
		return ret;
	}
	

	@RequestMapping(value="company/techCharts/growth")
	public GrowthOverPriorYears getGrowthChartsData(@RequestBody IdSearch search) throws CompanyServiceException, SearchServiceException  {		
		ChartRequestModel req = new ChartRequestModel();
		req.setChartDomain(ChartDomain.GROWTH_OVER_PRIOR_YEAR);
		req.setId(search.getId());
		@SuppressWarnings("unchecked")
		List<GrowthOverPriorYear> hits = (List<GrowthOverPriorYear>) companyService.loadChartData(req);
		GrowthOverPriorYears ret = new GrowthOverPriorYears();
		ret.setGrowthOverPriorYears(hits);
		return ret;
	}
	

	@RequestMapping(value="company/techCharts/cashFlow")
	public CashFlows getCashFlowChartsData(@RequestBody IdSearch search) throws CompanyServiceException, SearchServiceException  {		
		ChartRequestModel req = new ChartRequestModel();
		req.setChartDomain(ChartDomain.CASH_FLOWS);
		req.setId(search.getId());
		@SuppressWarnings("unchecked")
		List<CashFlow> hits = (List<CashFlow>) companyService.loadChartData(req);
		CashFlows ret = new CashFlows();
		ret.setCashFlows(hits);
		return ret;
	}

	@RequestMapping(value="company/gtis")
	public GovTransparencyIndexes getGtis(@RequestBody IdSearch search) throws CompanyServiceException {		
		return companyService.loadGtis(search.getId());
	}

	@RequestMapping("company/priceHistory")
	public PriceHistory getHistory(@RequestBody IdSearch search) throws CompanyServiceException {
		PriceHistory ret = new PriceHistory();
		ret.setPrice(companyService.loadPriceHistory(search.getId()));
		ret.setHighPrice(companyService.loadHighPriceHistory(search.getId()));
		ret.setLowPrice(companyService.loadLowPriceHistory(search.getId()));
		ret.setOpenPrice(companyService.loadOpenPriceHistory(search.getId()));
		ret.setVolume(companyService.loadVolumeHistory(search.getId()));
		return ret;
	}
	
	@RequestMapping("company/dividendHistory")
	public DividendHistory getDividendHistory(@RequestBody IdSearch search) throws CompanyServiceException {
		DividendHistory ret = new DividendHistory();
		return companyService.loadDividendHistory(search.getId());
	}

	@RequestMapping(value="company/alphaFactor")
	public AlphaFactor getAlphas(@RequestBody IdSearch search) throws CompanyServiceException {		
		return companyService.loadAlphaFactors(search.getId());
	}

	@RequestMapping(value="company/relatedCompanies")
	public SearchResults getRelatedCompanies(@RequestBody IdSearch search) throws CompanyServiceException{		
		User u = null;
		AccountType accountType=null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication.getPrincipal() instanceof UserDetailsWrapper){
			u = ((UserDetailsWrapper) authentication.getPrincipal()).getUser();
			AccountModel accountModel =  accountService.getAccountForUsername(u.getUsername());
			accountType = accountModel.getType();
				
		}else{
			
			accountType = AccountType.NOT_LOGGED_IN;
		}
		SearchResults searchResults = new SearchResults();
		List<SearchCompany> searchCompanies = new ArrayList<SearchCompany>();

		// Get related companies
		List<Company> companies = companyService.loadRelatedCompanies(search.getId(), accountType);
		
		if(companies != null){
			// Convert to correct response type
			searchCompanies = mapper.mapList(companies, SearchCompany.class);			
		}
		
		searchResults.setCompanies(searchCompanies);
		return searchResults;		
	}
}
