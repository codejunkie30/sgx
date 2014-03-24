package com.wmsi.sgx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.chart.ChartDataRequest;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.impl.DistributionService;
import com.wmsi.sgx.service.search.elasticsearch.Aggregations;

@RestController
@RequestMapping(produces="application/json")
public class StockScreenerController{
	
	@Autowired 
	private DistributionService distributionService;
	
	@RequestMapping("search/screener")
	public Aggregations getChartHistograms(@RequestBody ChartDataRequest req) throws ServiceException{
		
		Aggregations aggregations = distributionService.getAggregations(req.getFields());
		
		return aggregations;
	}	
}
