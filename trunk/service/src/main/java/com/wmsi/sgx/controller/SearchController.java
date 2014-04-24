package com.wmsi.sgx.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.search.CompanySearchRequest;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.service.CompanySearchService;
import com.wmsi.sgx.service.ServiceException;

@RestController()
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class SearchController{

	@Autowired
	private CompanySearchService companySearchService;
	
	@RequestMapping("search")
	public SearchResults search(@Valid @RequestBody SearchRequest req) throws ServiceException{
		return companySearchService.search(req);
	}	
	
	@RequestMapping("search/name")
	public SearchResults searchCompaniesByName(@Valid @RequestBody CompanySearchRequest req) throws ServiceException{		
		return companySearchService.searchCompaniesByName(req);
	}

}
