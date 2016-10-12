package com.wmsi.sgx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.estimates.EstimatesRequest;
import com.wmsi.sgx.service.EstimatesService;
import com.wmsi.sgx.service.ServiceException;

/**
 * This controller is used to fetch estimates based on Ticker code
 *
 */
@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class EstimatesController {
	
	@Autowired
	private EstimatesService estimateService;
	
	/**
	 * Fetches estimates based on the ticker code.
	 * 
	 * @param search
	 * @return Estimates
	 * @throws ServiceException
	 */
	@RequestMapping("search/estimates")
	public Estimates searchEstimates(@RequestBody EstimatesRequest search) throws ServiceException{

		return estimateService.search(search);
	}

}
