package com.wmsi.sgx.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.distribution.Distributions;
import com.wmsi.sgx.model.distribution.DistributionsRequest;
import com.wmsi.sgx.service.DistributionService;
import com.wmsi.sgx.service.ServiceException;

@RestController
@RequestMapping(produces="application/json")
public class DistributionsController{
	
	@Autowired 
	private DistributionService distributionService;

	@RequestMapping(value="search/distributions", method = RequestMethod.POST)
	public Distributions postChartHistograms(@Valid @RequestBody DistributionsRequest req) throws ServiceException{		
		return distributionService.getAggregations(req);
	}	
}
