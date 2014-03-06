package com.wmsi.sgx.service.sandp.capiq;

import java.util.List;

import com.wmsi.sgx.model.CompanyFinancial;
import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;

public interface CapIQService{
	CompanyInfo getCompanyInfo(String id) throws CapIQRequestException;

	List<List<HistoricalValue>> getHistoricalData(String id, String asOfDate) throws CapIQRequestException;

	CompanyFinancial getCompanyFinancials(String id, String period) throws CapIQRequestException;
}