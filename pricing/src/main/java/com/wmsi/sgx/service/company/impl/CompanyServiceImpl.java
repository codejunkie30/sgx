package com.wmsi.sgx.service.company.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.service.company.CompanyService;
import com.wmsi.sgx.service.company.CompanyServiceException;

@Service
public class CompanyServiceImpl implements CompanyService{

	private static final String ES_URL_TEMPLATE = "{0}/{1}/{2}/_search";	
	private static final String ES_TICKER_FIELD = "tickerCode";
	private static final String ES_TYPE = "company";
	
	@Value("${elasticsearch.index.name}")
	private String indexName;

	@Value("${elasticsearch.url}")
	private String url;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	private String restUrl;
	
	@PostConstruct
	public void init(){
		
		restUrl = MessageFormat
					.format(ES_URL_TEMPLATE, 
						url, 
						indexName,
						ES_TYPE);
	}
	
	/**
	 * Get all valid ticker
	 * @param 
	 * @return List of valid tickers
	 * @throws CompanyServiceException
	 */
	@Override
	public List<String> getAllTickers() throws CompanyServiceException{

		try{
			// Build fields query.
			String query = new SearchSourceBuilder().query(
							QueryBuilders.matchAllQuery())
								.size(10000)
								.fields(ES_TICKER_FIELD)
								.fetchSource(false)
								.toString();
			
			// Execute request to elasticsearch. 
			String json = restTemplate.postForObject(restUrl, query, String.class);

			return getTickersFromJson(json);
			
		}
		catch(IOException e){
			throw new CompanyServiceException("Couldn't fetch tickers from elasticsearch", e);
		}	
	}
	
	/**
	 * Parse out list of tickers from elasticsearch json results
	 */
	private List<String> getTickersFromJson(String json) throws JsonProcessingException, IOException{

		JsonNode node = mapper.readTree(json);

		// Find all nodes for field
		List<JsonNode> nodes = node.findValues(ES_TICKER_FIELD);
		
		List<String> tickers = new ArrayList<String>();
		
		// Collect text value of all nodes found 
		for(JsonNode n : nodes){
			Iterator<JsonNode> i = n.iterator();

			while(i.hasNext()){
				
				JsonNode value = i.next();
				String val = value.asText();
				
				if(!StringUtils.isEmpty(val))
					tickers.add(val);
			}
		}
		
		return tickers;
	}

}
