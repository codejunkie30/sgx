package com.wmsi.sgx.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.alpha.AlphaFactorSearchRequest;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.elasticsearch.query.AlphaFactorIdQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.AlphaFactorQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.AlphaFactorSearchQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.FinancialsQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.HistoricalValueQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.RelatedCompaniesQueryBuilder;
import com.wmsi.sgx.service.search.impl.SearchServiceImpl;

@Configuration
public class SearchConfig{
	
	@Value("${elasticsearch.index.name}")
	private String indexName;
	
	@Bean
	public SearchService<CompanyInfo> companySearchService(){
		SearchServiceImpl<CompanyInfo> serv = new SearchServiceImpl<CompanyInfo>();
		serv.setIndexName(indexName);
		serv.setType("company");

		return serv;
	}
	
	@Bean
	public SearchService<List<AlphaFactor>> alphaFactorService(){
		SearchServiceImpl<List<AlphaFactor>> serv = new SearchServiceImpl<List<AlphaFactor>>();
		serv.setIndexName(indexName);
		serv.setType("company");
		serv.setQueryBuilder(new AlphaFactorQueryBuilder());
		return serv;
	}

	@Bean
	public SearchService<AlphaFactorSearchRequest> alphaFactorSearchService(){
		SearchServiceImpl<AlphaFactorSearchRequest> serv = new SearchServiceImpl<AlphaFactorSearchRequest>();
		serv.setIndexName(indexName);
		serv.setType("alphaFactor");
		serv.setQueryBuilder(new AlphaFactorSearchQueryBuilder());
		return serv;
	}
	
	@Bean 
	public SearchService<CompanyInfo> relatedCompaniesSearch(){
		SearchServiceImpl<CompanyInfo> serv = new SearchServiceImpl<CompanyInfo>();
		serv.setIndexName(indexName);
		serv.setType("company");
		serv.setQueryBuilder(new RelatedCompaniesQueryBuilder());
		return serv;		
	}

	@Bean 
	public SearchService<String> alphaFactorIdSearch(){
		SearchServiceImpl<String> serv = new SearchServiceImpl<String>();
		serv.setIndexName(indexName);
		serv.setType("alphaFactor");
		serv.setQueryBuilder(new AlphaFactorIdQueryBuilder());
		return serv;		
	}

	@Bean 
	public SearchService<String> priceHistorySearch(){
		SearchServiceImpl<String> serv = new SearchServiceImpl<String>();
		serv.setIndexName(indexName);
		serv.setType("price");
		serv.setQueryBuilder(new HistoricalValueQueryBuilder());
		return serv;		
	}

	@Bean 
	public SearchService<String> volumeHistorySearch(){
		SearchServiceImpl<String> serv = new SearchServiceImpl<String>();
		serv.setIndexName(indexName);
		serv.setType("volume");
		serv.setQueryBuilder(new HistoricalValueQueryBuilder());
		return serv;		
	}

	@Bean 
	public SearchService<String> financialSearch(){
		SearchServiceImpl<String> serv = new SearchServiceImpl<String>();
		serv.setIndexName(indexName);
		serv.setType("financial");
		serv.setQueryBuilder(new FinancialsQueryBuilder());
		return serv;		
	}

	@Bean 
	public SearchService<String> holdersSearch(){
		SearchServiceImpl<String> serv = new SearchServiceImpl<String>();
		serv.setIndexName(indexName);
		serv.setType("holders");
		return serv;		
	}

	@Bean 
	public SearchService<String> keyDevsSearch(){
		SearchServiceImpl<String> serv = new SearchServiceImpl<String>();
		serv.setIndexName(indexName);
		serv.setType("keyDevs");
		return serv;		
	}
}
