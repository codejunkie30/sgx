package com.wmsi.sgx.service.impl;

import java.util.List;

import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.search.elasticsearch.Aggregations;

public interface DistributionService{

	Aggregations getAggregations(List<String> fields) throws ServiceException;

}