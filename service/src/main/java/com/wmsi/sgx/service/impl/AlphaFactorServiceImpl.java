package com.wmsi.sgx.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.model.search.Criteria;
import com.wmsi.sgx.model.search.Query;
import com.wmsi.sgx.model.search.Range;
import com.wmsi.sgx.model.search.RangeQuery;
import com.wmsi.sgx.model.search.TermQuery;
import com.wmsi.sgx.service.AlphaFactorService;
import com.wmsi.sgx.service.AlphaFactorServiceException;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@Service
public class AlphaFactorServiceImpl implements AlphaFactorService{

	@Autowired
	private SearchService alphaFactorSearchService;
	
	@Autowired
	private SearchService companySearchService;

	@Override
	public <T> List<T> search(String s, Class<T> clz) throws AlphaFactorServiceException {
		try{
			List<AlphaFactor> alphas = alphaFactorSearchService.search(s, AlphaFactor.class);
			System.out.println(buildTerms(alphas));
			return companySearchService.search(buildTerms(alphas), clz);
		}
		catch(SearchServiceException e){
			throw new AlphaFactorServiceException("Error loading company data.", e);
		}
	}
	
	// TODO Externalize
	public String buildTerms(List<AlphaFactor> alphas){
		ObjectMapper m = new ObjectMapper();
		Resource template = new ClassPathResource("META-INF/query/elasticsearch/template/constantScoreTermsFilter.json");

		try{
			ObjectNode oj = (ObjectNode) m.readTree(template.getFile());
			ArrayNode terms = ((ObjectNode)oj.findValue("terms")).putArray("gvKey");
			
			for(AlphaFactor a : alphas){
				terms.add(m.valueToTree("GV_".concat(a.getId().substring(0,6))));
			}
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

}
