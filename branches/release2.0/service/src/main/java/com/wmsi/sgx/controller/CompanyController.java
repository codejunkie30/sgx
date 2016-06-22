package com.wmsi.sgx.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
import com.wmsi.sgx.model.HistoricalValue;
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
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TokenHandler;
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
	
	@Autowired
	private MessageSource messages;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	private String defaultIndexName = "sgd_premium";
	
	@RequestMapping(value="company")
	public Map<String, Object> getAll(@RequestBody IdSearch search, HttpServletRequest request) throws CompanyServiceException, SearchServiceException {
		Boolean isPremiumUser = false;
		User u = null;
		String token = request.getHeader("X-AUTH-TOKEN");
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		
		if(token != null)
			u = tokenHandler.parseUserFromToken(token);
		isPremiumUser = accountService.isPremiumUser(u) || "pdf".equals(search.getType());
		Map<String, Object> ret = new HashMap<String, Object>();
		
		if(isPremiumUser){
			ret.put("company", getCompany(search,request));
			ret.put("holders", getHolders(search));
			ret.put("keyDevs", getKeyDevs(search).getKeyDevs());
			ret.put("alphaFactors", getAlphas(search,request));
			ret.put("gtis", getGtis(search));
			ret.put("dividendHistory", getDividendHistory(search,request));
		}else{
			if(companyService.isCompanyNonPremium(search.getId())){
				ret.put("company", getCompany(search,request));
				ret.put("holders", getHolders(search));
				ret.put("keyDevs", getKeyDevs(search).getKeyDevs());
				ret.put("alphaFactors", getAlphas(search,request));
				ret.put("gtis", getGtis(search));
				ret.put("dividendHistory", getDividendHistory(search,request));	
			}else{
				ret.put("errorCode", (messages.getMessage("user.company.no.access.errorCode",null,LocaleContextHolder.getLocale())));
				ret.put("errorMessage", (messages.getMessage("user.company.no.access",null,LocaleContextHolder.getLocale())));
			}
		}
		return ret;
	}
	
	@RequestMapping(value="company/techChart")
	public Map<String, Object> getTechChart(@RequestBody IdSearch search, HttpServletRequest request) throws CompanyServiceException, SearchServiceException {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("company", getCompany(search,request));
		ret.put("dividendHistory", getDividendHistory(search,request));
		ret.put("financials", getFinancials(search, request));
		ret.put("estimates",getEstimates(search,request));
		return ret;
	}
	
	@RequestMapping(value="company/info")
	public Map<String, Company> getCompany(@RequestBody IdSearch search, HttpServletRequest request) throws CompanyServiceException{
		
		String currency =setCurrency(request);
		
		Map<String, Company> ret = new HashMap<String, Company>();
		Company comp = companyService.getCompanyByIdAndIndex(search.getId(),currency.toLowerCase()+"_premium");
		comp.setFilingCurrency(currency.toUpperCase());
		ret.put("companyInfo", comp);
		return ret;
	}
	
	private String getIndexName(HttpServletRequest request){
		String index = defaultIndexName;
				if(request.getHeader("currency") != null){
					index = request.getHeader("currency").toLowerCase() +"_premium" ;
				}
		return index;		
	}
	
	@RequestMapping(value="company/keyDevs")
	public KeyDevs getKeyDevs(@RequestBody IdSearch search) throws CompanyServiceException{
		KeyDevs keydevs = companyService.loadKeyDevs(search.getId(), defaultIndexName);
		
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
	public Financials getFinancials(@RequestBody IdSearch search, HttpServletRequest request) throws CompanyServiceException {
		String currency = setCurrency(request);
		Boolean isPremiumUser = false;
		User u = null;
		String token = request.getHeader("X-AUTH-TOKEN");
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		
		if(token != null)
			u = tokenHandler.parseUserFromToken(token);
		isPremiumUser = accountService.isPremiumUser(u) || "pdf".equals(search.getType());;
		List<Financial> hits = companyService.loadFinancials(search.getId(),currency);
		
		for(Financial fn : hits ){
			if(isPremiumUser){
				fn.setFilingCurrency(currency.toUpperCase());
			}else {
				fn.setFilingCurrency("SGD");
			}
		}
		Financials ret = new Financials();
		ret.setFinancials(hits);
		return ret;
	}
	
	@RequestMapping(value="company/estimates")
	public Estimates getEstimates(@RequestBody IdSearch search, HttpServletRequest request) throws CompanyServiceException {
		String currency = setCurrency(request);
		List<Estimate> hits = companyService.loadEstimates(search.getId(),currency);
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
	public PriceHistory getHistory(@RequestBody IdSearch search, HttpServletRequest request) throws CompanyServiceException {
		String currency = setCurrency(request);
		PriceHistory ret = new PriceHistory();
		
		List<HistoricalValue> price = companyService.loadPriceHistory(search.getId(),currency);
		List<HistoricalValue> highPrice= companyService.loadHighPriceHistory(search.getId(),currency);
		List<HistoricalValue> lowPrice = companyService.loadLowPriceHistory(search.getId(),currency);
		List<HistoricalValue> openPrice = companyService.loadOpenPriceHistory(search.getId(),currency);
		List<HistoricalValue> volume = companyService.loadVolumeHistory(search.getId(),currency);
		
		setData(price,highPrice,lowPrice,openPrice,volume);
		
		ret.setPrice(price);
		ret.setHighPrice(highPrice);
		ret.setLowPrice(lowPrice);
		ret.setOpenPrice(openPrice);
		ret.setVolume(volume);
		return ret;
	}
	
	/***
	 * Method to add missed data for a particular date.
	 * @param price
	 * @param highPrice
	 * @param lowPrice
	 * @param openPrice
	 * @param volume
	 */
	/***
	 * Method to add missed data for a particular date.
	 * @param price
	 * @param highPrice
	 * @param lowPrice
	 * @param openPrice
	 * @param volume
	 */
  private void setData( List<HistoricalValue> price,
                        List<HistoricalValue> highPrice,
                        List<HistoricalValue> lowPrice,
                        List<HistoricalValue> openPrice,
                        List<HistoricalValue> volume )
  {
    
    int priceListSize = price.size();
    int highPriceListSize = highPrice.size();
    int lowPriceListSize = lowPrice.size();
    int openPriceListSize = openPrice.size();
    int volumeListSize = volume.size();
    if( priceListSize != highPriceListSize || priceListSize != lowPriceListSize
        || priceListSize != openPriceListSize
        || priceListSize != volumeListSize )
    {
      if( priceListSize != highPriceListSize )
      {
        for( int i = 0; i < price.size(); i++ )
        {
          HistoricalValue priceH = price.get( i );
          HistoricalValue priceHP = highPrice.get( i );
          HistoricalValue temp = null;
          if( !priceH.getDate().equals( priceHP.getDate() ) )
          {
            temp = new HistoricalValue();
            temp.setDate( priceH.getDate() );
            temp.setTickerCode( priceH.getTickerCode() );
            temp.setValue( 0.0 );
            highPrice.add( i, temp );
          }
        }
      }
      if( priceListSize != lowPriceListSize )
      {
        for( int i = 0; i < price.size(); i++ )
        {
          HistoricalValue priceH = price.get( i );
          HistoricalValue priceLP = lowPrice.get( i );
          HistoricalValue temp = null;
          if( !priceH.getDate().equals( priceLP.getDate() ) )
          {
            temp = new HistoricalValue();
            temp.setDate( priceH.getDate() );
            temp.setTickerCode( priceH.getTickerCode() );
            temp.setValue( 0.0 );
            lowPrice.add( i, temp );
          }
          
        }
      }
      if( priceListSize != openPriceListSize )
      {
        for( int i = 0; i < price.size(); i++ )
        {
          HistoricalValue priceH = price.get( i );
          HistoricalValue priceOP = openPrice.get( i );
          HistoricalValue temp = null;
          if( !priceH.getDate().equals( priceOP.getDate() ) )
          {
            temp = new HistoricalValue();
            temp.setDate( priceH.getDate() );
            temp.setTickerCode( priceH.getTickerCode() );
            temp.setValue( 0.0 );
            openPrice.add( i, temp );
          }
        }
      }
      if( priceListSize != volumeListSize )
      {
        for( int i = 0; i < price.size(); i++ )
        {
          HistoricalValue priceH = price.get( i );
          HistoricalValue volumev = volume.get( i );
          HistoricalValue temp = null;
          if( !priceH.getDate().equals( volumev.getDate() ) )
          {
            temp = new HistoricalValue();
            temp.setDate( priceH.getDate() );
            temp.setTickerCode( priceH.getTickerCode() );
            temp.setValue( 0.0 );
            volume.add( i, temp );
          }
        }
      }
      
    }
  }
	
	@RequestMapping("company/dividendHistory")
	public DividendHistory getDividendHistory(@RequestBody IdSearch search, HttpServletRequest request) throws CompanyServiceException {
		return companyService.loadDividendHistory(search.getId(),setCurrency(request));
	}

	@RequestMapping(value="company/alphaFactor")
	public AlphaFactor getAlphas(@RequestBody IdSearch search, HttpServletRequest request) throws CompanyServiceException {		
		return companyService.loadAlphaFactors(search.getId(),setCurrency(request));
	}

	@RequestMapping(value="company/relatedCompanies")
	public SearchResults getRelatedCompanies(@RequestBody IdSearch search, HttpServletRequest request) throws CompanyServiceException{		
		User u = null;
		AccountType accountType=null;
		String token = request.getHeader("X-AUTH-TOKEN");
		
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		
		if(token != null){
		u = tokenHandler.parseUserFromToken(token);
		AccountModel accountModel =  accountService.getAccountForUsername(u.getUsername());
		accountType = accountModel.getType();	
		}else{
			
			accountType = AccountType.NOT_LOGGED_IN;
		}
		SearchResults searchResults = new SearchResults();
		List<SearchCompany> searchCompanies = new ArrayList<SearchCompany>();

		// Get related companies
		List<Company> companies = companyService.loadRelatedCompanies(search.getId(), accountType,setCurrency(request));
		
		if(companies != null){
			// Convert to correct response type
			searchCompanies = mapper.mapList(companies, SearchCompany.class);			
		}
		
		searchResults.setCompanies(searchCompanies);
		return searchResults;		
	}
	
	public String setCurrency(HttpServletRequest request){
		if(request.getHeader("currency") != null)
			return request.getHeader("currency");
		else
			return "sgd";
	}
}
