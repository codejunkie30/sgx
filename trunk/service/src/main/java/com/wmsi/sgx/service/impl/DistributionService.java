package com.wmsi.sgx.service.impl;

import java.util.List;

import com.wmsi.sgx.model.distribution.Distributions;
import com.wmsi.sgx.service.ServiceException;

public interface DistributionService{

	Distributions getAggregations(List<String> fields) throws ServiceException;

}