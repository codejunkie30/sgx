package com.wmsi.sgx.controller.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@RestController
@RequestMapping(produces="application/json")
public class SearchController{

	@Autowired
	private SearchService companySearchService;
	
	@RequestMapping("search")
	public SearchResults search(@RequestBody SearchRequest req) throws SearchServiceException{
		
		String query = buildQuery(req);
		List<SearchCompany> companies = companySearchService.search(query, SearchCompany.class);
		SearchResults results = new SearchResults();
		results.setCompanies(companies);
		return results;
	}	
	
	private String buildQuery(SearchRequest req){
		return "{\"query\":{\"match_all\": {}},\"size\": 1000}";
	}

}
