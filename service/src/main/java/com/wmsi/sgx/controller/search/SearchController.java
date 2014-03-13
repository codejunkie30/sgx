package com.wmsi.sgx.controller.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.search.elasticsearch.ESQuery;
import com.wmsi.sgx.service.search.elasticsearch.ESResponse;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.SearchQuery;

public class SearchController{

	@RequestMapping("search")
	public SearchResults search(){
		/*
		CompanyInfo info = loadCompany("A7S");
		SearchCompany res = new SearchCompany();
		res.setCode(info.getTickerCode());
		res.setCompanyName(info.getCompanyName());
		res.setDividendYield(info.getDividendYield());
		res.setMarketCap(info.getMarketCap());
		res.setIndustry(info.getIndustry());
		res.setPeRatio(info.getPeRatio());
		res.setTotalRevenue(info.getTotalRevenue());
		
		SearchResults results = new SearchResults();
		results.setCompanies(Arrays.asList(new SearchCompany[]{res}));
		return results;
		*/
		return null;
	}	

}
