package com.wmsi.sgx.service.sandp.capiq;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;

public interface CapIQService{
	PriceHistory getHistoricalData(String id, String asOfDate) throws ResponseParserException, CapIQRequestException;

	Company getCompanyInfo(String id, String startDate) throws ResponseParserException, CapIQRequestException;

	Holders getHolderDetails(String ticker) throws ResponseParserException, CapIQRequestException;

	KeyDevs getKeyDevelopments(String id, String asOfDate) throws ResponseParserException, CapIQRequestException;

	Financials getCompanyFinancials(String id, String currency) throws ResponseParserException, CapIQRequestException;
}