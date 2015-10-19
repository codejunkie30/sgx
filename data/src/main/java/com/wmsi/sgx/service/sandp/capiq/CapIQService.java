package com.wmsi.sgx.service.sandp.capiq;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.integration.CompanyInputRecord;

public interface CapIQService{
	
	PriceHistory getHistoricalData(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException;

	Company getCompany(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException;

	Holders getHolderDetails(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException;

	KeyDevs getKeyDevelopments(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException;

	Financials getCompanyFinancials(CompanyInputRecord input, String currency) throws ResponseParserException, CapIQRequestException;

	DividendHistory getDividendData(CompanyInputRecord input)
			throws ResponseParserException, CapIQRequestException;
	
	Estimates getEstimates(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException;

}