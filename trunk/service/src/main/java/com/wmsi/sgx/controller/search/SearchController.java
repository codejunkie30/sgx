package com.wmsi.sgx.controller.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.search.CompanySearchRequest;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.service.search.Search;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@RestController
@RequestMapping(produces="application/json")
public class SearchController{

	@Autowired
	private SearchService companySearchService;
	
	@RequestMapping("search")
	public SearchResults search(@RequestBody SearchRequest req) throws SearchServiceException{
		String query = req.buildQuery();
		List<SearchCompany> companies = companySearchService.search(query, SearchCompany.class);
		SearchResults results = new SearchResults();
		results.setCompanies(companies);
		return results;
	}	

	@Autowired
	private Search<SearchCompany> companyNameSearch;

	@RequestMapping("search/name")
	public SearchResults searchCompaniesByName(@RequestBody CompanySearchRequest req) throws SearchServiceException{		
		
		// TODO Extend HashMap, BindParms(k,v) with put(k,v)->return this
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("search", req.getCompanyName());		
		
		List<SearchCompany> companies= companySearchService.search(companyNameSearch, m);
		SearchResults results = new SearchResults();
		results.setCompanies(companies);
		return results;
	}

}
