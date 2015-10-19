package com.wmsi.sgx.service;

import java.util.List;

import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.Estimate;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.GovTransparencyIndexes;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;

public interface CompanyService{

	KeyDevs loadKeyDevs(String id) throws CompanyServiceException;

	Holders loadHolders(String id) throws CompanyServiceException;

	Company getById(String id) throws CompanyServiceException;

	AlphaFactor loadAlphaFactors(String id) throws CompanyServiceException;

	List<Company> loadRelatedCompanies(String id) throws CompanyServiceException;

	List<HistoricalValue> loadVolumeHistory(String search) throws CompanyServiceException;

	List<HistoricalValue> loadPriceHistory(String search) throws CompanyServiceException;
	
	List<HistoricalValue> loadHighPriceHistory(String search) throws CompanyServiceException;
	
	List<HistoricalValue> loadLowPriceHistory(String search) throws CompanyServiceException;
	
	List<HistoricalValue> loadOpenPriceHistory(String search) throws CompanyServiceException;

	List<Financial> loadFinancials(String id) throws CompanyServiceException;

	GovTransparencyIndexes loadGtis(String id) throws CompanyServiceException;

	DividendHistory loadDividendHistory(String id) throws CompanyServiceException;
	
	List<Estimate> loadEstimates(String id) throws CompanyServiceException;

}