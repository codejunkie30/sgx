package com.wmsi.sgx.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wmsi.sgx.model.sandp.alpha.AlphaSearchRequest;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.model.search.TermQuery;
import com.wmsi.sgx.service.AlphaFactorService;
import com.wmsi.sgx.service.AlphaFactorServiceException;

@RestController
@RequestMapping(produces="application/json")
public class AlphaFactorsController{

	@Autowired
	private AlphaFactorService alphaFactorService;
	
	@RequestMapping("search/alphaFactors")
	public SearchResults searchAlphaFactors(@RequestBody AlphaSearchRequest search) throws AlphaFactorServiceException{
		
		String query = buildQuery(search);
		System.out.println(query);
		List<SearchCompany> companies = alphaFactorService.search(query, SearchCompany.class);
		SearchResults results = new SearchResults();
		results.setCompanies(companies);
		return results;
	}
	
	ObjectMapper m = new ObjectMapper();
	public String buildQuery(AlphaSearchRequest req){
		
		Resource template = new ClassPathResource("META-INF/query/elasticsearch/template/constantScoreBooleanFilter.json");

		try{
			ObjectNode oj = (ObjectNode) m.readTree(template.getFile());
			ArrayNode must = (ArrayNode)oj.findValue("must");
			
			addTerm(must, "analystExpectations", req.getAnalystExpectations());
			addTerm(must, "capitalEfficiency", req.getCapitalEfficiency());
			addTerm(must, "earningsQuality",req.getEarningsQuality());
			addTerm(must, "historicalGrowth",req.getHistoricalGrowth());
			addTerm(must, "priceMomentum",req.getPriceMomentum());
			addTerm(must, "size",req.getSize());
			addTerm(must, "valuation",req.getValuation());
			addTerm(must, "volatility",req.getVolatility());
			
			return m.writeValueAsString(oj);
		}
		catch(JsonProcessingException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private void addTerm(ArrayNode arr, String field, Integer value){
		if(StringUtils.isNotEmpty(field) && value != null){
			TermQuery r = new TermQuery();
			r.setField(field);
			r.setValue(String.valueOf(value));
			
			arr.add(m.valueToTree(r));
		}
		
	}
}
