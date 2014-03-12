package com.wmsi.sgx.service.sandp.capiq;

import java.text.ParseException;
import java.util.List;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holder;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;

public interface CapIQService{
	List<List<HistoricalValue>> getHistoricalData(String id, String asOfDate) throws CapIQRequestException;

	CompanyFinancial getCompanyFinancials(String id, String period) throws CapIQRequestException;

	CompanyInfo getCompanyInfo(String id, String startDate) throws CapIQRequestException;

	Holders getHolderDetails(String ticker) throws CapIQRequestException;

	KeyDevs getKeyDevelopments(String id, String asOfDate) throws CapIQRequestException;
}