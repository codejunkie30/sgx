package com.wmsi.sgx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.distribution.Distributions;
import com.wmsi.sgx.model.distribution.DistributionsRequest;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.impl.DistributionService;

@RestController
@RequestMapping(produces="application/json")
public class DistributionsController{
	
	@Autowired 
	private DistributionService distributionService;

	@Autowired
	private ObjectMapper esObjectMapper;

	@RequestMapping(value="search/distributions", method = RequestMethod.POST)
	public Distributions postChartHistograms(@RequestBody DistributionsRequest req) throws ServiceException{		
		return distributionService.getAggregations(req.getFields());
	}	
}
