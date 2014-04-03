package com.wmsi.sgx.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.alpha.AlphaFactorSearchRequest;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.service.AlphaFactorService;
import com.wmsi.sgx.service.AlphaFactorServiceException;

@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class AlphaFactorsController{

	@Autowired
	private AlphaFactorService alphaFactorService;
	
	@RequestMapping("search/alphaFactors")
	public SearchResults searchAlphaFactors(@RequestBody AlphaFactorSearchRequest search) throws AlphaFactorServiceException{

		List<SearchCompany> companies = alphaFactorService.search(search, SearchCompany.class);
		SearchResults results = new SearchResults();
		results.setCompanies(companies);
		
		return results;
	}
	
}
