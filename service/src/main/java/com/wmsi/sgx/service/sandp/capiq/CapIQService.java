package com.wmsi.sgx.service.sandp.capiq;

import java.util.List;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;

public interface CapIQService{
	List<List<HistoricalValue>> getHistoricalData(String id, String asOfDate) throws CapIQRequestException;

	CompanyInfo getCompanyInfo(String id, String startDate) throws CapIQRequestException;

	Holders getHolderDetails(String ticker) throws CapIQRequestException;

	KeyDevs getKeyDevelopments(String id, String asOfDate) throws CapIQRequestException;

	List<CompanyFinancial> getCompanyFinancials(String id) throws CapIQRequestException;
}