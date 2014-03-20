package com.wmsi.sgx.service;

import java.util.List;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;

public interface CompanyService{

	KeyDevs loadKeyDevs(String id) throws CompanyServiceException;

	Holders loadHolders(String id) throws CompanyServiceException;

	List<CompanyFinancial> loadFinancials(String id) throws CompanyServiceException;

	List<HistoricalValue> loadPriceHistory(String id) throws CompanyServiceException;

	List<HistoricalValue> loadVolumeHistory(String id) throws CompanyServiceException;

	CompanyInfo getById(String id, Class<CompanyInfo> class1) throws CompanyServiceException;

	AlphaFactor loadAlphaFactors(String id) throws CompanyServiceException;

	List<CompanyInfo> loadRelatedCompanies(String id) throws CompanyServiceException;

}