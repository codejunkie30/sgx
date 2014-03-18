package com.wmsi.sgx.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.histogram.CompanyHistogram;
import com.wmsi.sgx.model.histogram.Histogram;
import com.wmsi.sgx.model.screener.ScreenerResponse;
import com.wmsi.sgx.service.search.elasticsearch.Aggregation;
import com.wmsi.sgx.service.search.elasticsearch.Aggregations;
import com.wmsi.sgx.service.search.elasticsearch.Bucket;
import com.wmsi.sgx.service.search.elasticsearch.ESResponse;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;

@RestController
@RequestMapping(produces="application/json")
public class StockScreenerController{
	
	String indexName = "sgx_test";
	
	@Autowired
	private ElasticSearchService elasticSearchService;
	
	@RequestMapping("search/screener")
	public ScreenerResponse getChartHistograms() throws IOException, ElasticSearchException{
		
		Resource chartDataTemplate = new ClassPathResource("META-INF/query/elasticsearch/chartData.json");
		String queryTemplate = FileUtils.readFileToString(chartDataTemplate.getFile());		
		
		ESResponse response = elasticSearchService.search(indexName, queryTemplate, new HashMap<String, Object>());
		Aggregations aggregations = response.getAggregations();
		
		List<String> industries = buildIndustries(aggregations, "industry");
		List<String> industryGroup = buildIndustries(aggregations, "industryGroup");
		
		ScreenerResponse screener = new ScreenerResponse();
		screener.setIndustries(industries);
		screener.setIndustyGroups(industryGroup);
		screener.setHistograms(loadHistograms(aggregations));
		return screener;
	}
	
	private Aggregation getAggregation(Aggregations aggs, String name){
		
		for(Aggregation agg : aggs.getAggregations()){
			if(agg.getName().equals(name))
				return agg;
		}
		
		return null;
	}
	
	private List<Histogram> buildHistogram(Aggregation a){
		
		List<Histogram> ret = new ArrayList<Histogram>();
		
		for(Bucket b : a.getBuckets()){
			Histogram h = new Histogram();
			h.setCount(b.getCount());
			h.setKey(b.getKey());
			ret.add(h);
		}
		
		return ret;
	}
	
	private List<String> buildIndustries(Aggregations a, String name){
		Aggregation ind = getAggregation(a, name);
		
		List<String> ret = new ArrayList<String>();
		
		for(Bucket b : ind.getBuckets()){
			ret.add(b.getKey().toString());
		}
		
		return ret;
	}
	
	private CompanyHistogram loadHistograms(Aggregations aggs){
		
		Aggregation marketCap = getAggregation(aggs, "marketCap");
		Aggregation totRev = getAggregation(aggs, "totalRevenue");
		Aggregation div = getAggregation(aggs, "dividendYield");
		Aggregation pe = getAggregation(aggs, "peRatio");
		
		CompanyHistogram ch = new CompanyHistogram();
		ch.setMarketCap(buildHistogram(marketCap));
		ch.setDividendYield(buildHistogram(div));
		ch.setPeRatio(buildHistogram(pe));
		ch.setTotalRevenue(buildHistogram(totRev));
		return ch;
	}
}
