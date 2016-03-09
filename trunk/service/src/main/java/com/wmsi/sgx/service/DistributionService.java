package com.wmsi.sgx.service;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.distribution.Distributions;
import com.wmsi.sgx.model.distribution.DistributionsRequest;

public interface DistributionService{

	Distributions getAggregations(DistributionsRequest req, String currency, AccountType accType) throws ServiceException;

}