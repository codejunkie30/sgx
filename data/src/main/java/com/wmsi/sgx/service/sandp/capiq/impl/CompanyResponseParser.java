package com.wmsi.sgx.service.sandp.capiq.impl;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.service.sandp.capiq.CapIQServiceException;
import com.wmsi.sgx.service.sandp.capiq.InvalidIdentifierException;

public class CompanyResponseParser extends AbstractResponseParser{

	@Override
	protected Class<Company> getType() {
		return Company.class;
	}

	@SuppressWarnings("unchecked")
	public Company convert(CapIQResponse response) throws CapIQServiceException, InvalidIdentifierException {

		Company company = new Company();

		for(CapIQResult result : response.getResults()){
			parseResult(result, company);
		}
		
		return company;
	}
}
