package com.wmsi.sgx.service.sandp.capiq.impl;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.service.sandp.capiq.AbstractResponseParser;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

public class CompanyResponseParser extends AbstractResponseParser{

	@Override
	public Class<Company> getType() {
		return Company.class;
	}

	public Company convert(CapIQResponse response) throws ResponseParserException{

		Company company = new Company();

		for(CapIQResult result : response.getResults()){
			parseResult(result, company);
		}
		
		return company;
	}
}
