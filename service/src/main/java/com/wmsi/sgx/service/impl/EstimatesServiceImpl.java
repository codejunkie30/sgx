package com.wmsi.sgx.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.estimates.EstimatesRequest;
import com.wmsi.sgx.service.EstimatesService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@Service
public class EstimatesServiceImpl implements EstimatesService{
	
	@Autowired
	private SearchService estimatesSerach;
	
	public Estimates search(EstimatesRequest req) throws ServiceException {
		
		try{
			Estimates estimates = estimatesSerach.getById(req.getTickerCode(), Estimates.class);
			return estimates;
			
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}
	
}
