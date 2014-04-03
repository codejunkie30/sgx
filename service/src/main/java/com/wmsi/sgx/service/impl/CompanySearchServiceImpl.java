package com.wmsi.sgx.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.search.CompanySearchRequest;
import com.wmsi.sgx.model.search.Criteria;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.service.CompanySearchService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;
import com.wmsi.sgx.service.search.elasticsearch.query.TextSearchQueryBuilder;

@Service
public class CompanySearchServiceImpl implements CompanySearchService{

	private static final Logger log = LoggerFactory.getLogger(CompanySearchServiceImpl.class);

	@Autowired 
	private ElasticSearchService elasticSearchService;
	
	@Override
	public SearchResults search(SearchRequest req) throws ServiceException {

		String query = buildQuery(req.getCriteria());
		
		try{
			List<SearchCompany> companies = elasticSearchService.search("sgx_test", "company", query, SearchCompany.class);
			SearchResults results = new SearchResults();
			results.setCompanies(companies);
			return results;

		}
		catch(ElasticSearchException e){
			throw new ServiceException("Query execution failed", e);
		}		
	}

	@Override
	public SearchResults searchCompaniesByName(CompanySearchRequest req) throws SearchServiceException{		
		try{
			TextSearchQueryBuilder queryBuilder = new TextSearchQueryBuilder();

			List<SearchCompany> companies = elasticSearchService.search(
					"sgx_test",
					"company",
					queryBuilder.build(req.getCompanyName()), 
					SearchCompany.class);

			SearchResults results = new SearchResults();
			results.setCompanies(companies);
			return results;
		}
		catch(ElasticSearchException e){
			throw new SearchServiceException("Failure during company text search", e);
		}				
	}
	
	public String buildQuery(List<Criteria> criteria){
		
		BoolFilterBuilder boolFilter = FilterBuilders.boolFilter();
		
		for(Criteria c : criteria){
			
			if(c.getField().equals("percentChange")){
				
				boolFilter.must(FilterBuilders.scriptFilter(
					"prices = ($.value in _source.priceHistory if $.date >= from && $.date <= to); return prices.size() > 0 ? floor(abs((prices[prices.size()-1] - prices[0]) * 100)) == value : false"	
				)
				.addParam("to", getTime(c.getTo()))
				.addParam("from", getTime(c.getFrom()))
				.addParam("value", c.getValue()));				
			}
			else if(c.getTo() != null && c.getFrom() != null){
				boolFilter.must(FilterBuilders.rangeFilter(c.getField())
					.from(c.getFrom())
					.to(c.getTo()));					
			}
			else{
				boolFilter.must(FilterBuilders.termFilter(c.getField(), c.getValue()));
			}
		}
		
		return new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
					boolFilter.cache(true)
					))
			.size(2000)
			.toString();
	}

	private long getTime(Object object){
		try{
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			return fmt.parse(object.toString()).getTime();
		}
		catch(ParseException e){
			log.error("Couldn't not parse datetime from request", e);
			return 0;
		}
	}
}
