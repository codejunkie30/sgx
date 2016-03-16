package com.wmsi.sgx.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.CompanyNameAndTicker;
import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.GovTransparencyIndexes;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.IsCompanyNonPremiumModel;
import com.wmsi.sgx.model.search.ChartRequestModel;
import com.wmsi.sgx.service.search.SearchServiceException;

public interface CompanyService{

	KeyDevs loadKeyDevs(String id) throws CompanyServiceException;

	Holders loadHolders(String id) throws CompanyServiceException;

	Company getById(String id, String currency) throws CompanyServiceException;

	AlphaFactor loadAlphaFactors(String id,String currency) throws CompanyServiceException;

	List<Company> loadRelatedCompanies(String id, AccountType accType,String currency) throws CompanyServiceException;

	List<HistoricalValue> loadVolumeHistory(String search,String currency) throws CompanyServiceException;

	List<HistoricalValue> loadPriceHistory(String search,String currency) throws CompanyServiceException;
	
	List<HistoricalValue> loadHighPriceHistory(String search,String currency) throws CompanyServiceException;
	
	List<HistoricalValue> loadLowPriceHistory(String search,String currency) throws CompanyServiceException;
	
	List<HistoricalValue> loadOpenPriceHistory(String search,String currency) throws CompanyServiceException;

	List<Financial> loadFinancials(String id,String currency) throws CompanyServiceException;

	GovTransparencyIndexes loadGtis(String id) throws CompanyServiceException;

	DividendHistory loadDividendHistory(String id,String currency) throws CompanyServiceException;
	
	List<Estimate> loadEstimates(String id,String currency) throws CompanyServiceException;
	
	List<?> loadChartData(ChartRequestModel search) throws CompanyServiceException, SearchServiceException;

	List<CompanyNameAndTicker> loadCompanyNamesAndTickers() throws SearchServiceException;
	
	Boolean isCompanyNonPremium(String tickerCode) throws SearchServiceException;
	
	Company getPreviousById(String id) throws CompanyServiceException;

	List<Estimate> loadPreviousEstimates(String id)
			throws CompanyServiceException;

}