package com.wmsi.sgx.service;

import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.estimates.EstimatesRequest;

public interface EstimatesService {
	
	Estimates search(EstimatesRequest req) throws ServiceException;

}
