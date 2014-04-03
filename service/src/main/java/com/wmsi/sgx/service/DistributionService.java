package com.wmsi.sgx.service;

import java.util.List;

import com.wmsi.sgx.model.distribution.Distributions;

public interface DistributionService{

	Distributions getAggregations(List<String> fields) throws ServiceException;

}