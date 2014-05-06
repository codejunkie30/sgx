package com.wmsi.sgx.service.sandp.capiq;

import java.util.List;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.Financials;

public interface CapIQService{
	List<List<HistoricalValue>> getHistoricalData(String id, String asOfDate) throws ResponseParserException, CapIQRequestException;

	Company getCompanyInfo(String id, String startDate) throws ResponseParserException, CapIQRequestException;

	Holders getHolderDetails(String ticker) throws ResponseParserException, CapIQRequestException;

	KeyDevs getKeyDevelopments(String id, String asOfDate) throws ResponseParserException, CapIQRequestException;

	Financials getCompanyFinancials(String id, String currency) throws ResponseParserException, CapIQRequestException;
}