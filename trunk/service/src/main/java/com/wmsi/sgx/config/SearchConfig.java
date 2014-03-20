package com.wmsi.sgx.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.search.Search;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.impl.SearchServiceImpl;

@Configuration
public class SearchConfig{
	
	//TODO Move to properties
	private String indexName = "sgx_test";
	
	@Bean
	public SearchService companySearchService(){
		SearchServiceImpl serv = new SearchServiceImpl();
		serv.setIndexName(indexName);
		serv.setType("company");
		return serv;
	}
	
	@Bean
	public SearchService alphaFactorSearchService(){
		SearchServiceImpl serv = new SearchServiceImpl();
		serv.setIndexName(indexName);
		serv.setType("alphaFactor");
		return serv;
	}
	
	@Bean
	public Search<KeyDevs> keyDevsSearch() throws IOException{
		Search<KeyDevs>  s = new Search<KeyDevs>();
		s.setIndexName(indexName);
		s.setType("keyDevs");
		s.setResultClass(KeyDevs.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/keyDevs.json");
		s.setQuery(r.getFile());		
		return s;
	}
	
	@Bean
	public Search<Holders> holdersSearch() throws IOException{
		Search<Holders>  s = new Search<Holders>();
		s.setIndexName(indexName);
		s.setType("holders");
		s.setResultClass(Holders.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/holders.json");
		s.setQuery(r.getFile());		
		return s;
	}
	
	@Bean
	public Search<CompanyFinancial> financialSearch() throws IOException{
		Search<CompanyFinancial>  s = new Search<CompanyFinancial>();
		s.setIndexName(indexName);
		s.setType("financial");
		s.setResultClass(CompanyFinancial.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/financials.json");
		s.setQuery(r.getFile());		
		return s;	
	}
	
	@Bean
	public Search<HistoricalValue> priceSearch() throws IOException{
		Search<HistoricalValue>  s = new Search<HistoricalValue>();
		s.setIndexName(indexName);
		s.setType("price");
		s.setResultClass(HistoricalValue.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/historical.json");
		s.setQuery(r.getFile());		
		return s;				
	}

	@Bean
	public Search<HistoricalValue> volumeSearch() throws IOException{
		Search<HistoricalValue>  s = new Search<HistoricalValue>();
		s.setIndexName(indexName);
		s.setType("volume");
		s.setResultClass(HistoricalValue.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/historical.json");
		s.setQuery(r.getFile());		
		return s;			
	}

	@Bean
	public Search<AlphaFactor> alphaFactorSearch() throws IOException{
		Search<AlphaFactor>  s = new Search<AlphaFactor>();
		s.setIndexName(indexName);
		s.setType("alphaFactor");
		s.setResultClass(AlphaFactor.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/alphaFactor.json");
		s.setQuery(r.getFile());		
		return s;			
	}

}
