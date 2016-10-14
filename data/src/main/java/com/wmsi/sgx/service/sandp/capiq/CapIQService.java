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
	
	//Get historical Pricing data
	PriceHistory getHistoricalData(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException;
	
	//Get CompanyData based on CompanyInputRecord 
	Company getCompany(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException;
	
	//Get holders ownership data based on CompanyInputRecord
	Holders getHolderDetails(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException;
	
	//Get keyDevelopments data based on CompanyInputRecord
	KeyDevs getKeyDevelopments(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException;

	//Get financials data based on CompanyInputRecord
	Financials getCompanyFinancials(CompanyInputRecord input, String currency) throws ResponseParserException, CapIQRequestException;
	
	//Get Dividend data based on CompanyInputRecord
	DividendHistory getDividendData(CompanyInputRecord input)
			throws ResponseParserException, CapIQRequestException;
	
	//Get Estimates data based on CompanyInputRecord
	Estimates getEstimates(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException;

}