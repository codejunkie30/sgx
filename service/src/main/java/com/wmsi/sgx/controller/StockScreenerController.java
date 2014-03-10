package com.wmsi.sgx.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.histogram.CompanyHistogram;
import com.wmsi.sgx.model.histogram.Histogram;
import com.wmsi.sgx.model.screener.ScreenerResponse;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.model.search.input.IdSearch;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.search.elasticsearch.Aggregation;
import com.wmsi.sgx.service.search.elasticsearch.Aggregations;
import com.wmsi.sgx.service.search.elasticsearch.Bucket;
import com.wmsi.sgx.service.search.elasticsearch.ESQuery;
import com.wmsi.sgx.service.search.elasticsearch.ESQueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.ESResponse;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.SearchQuery;

@RestController
//@RequestMapping(value="search", produces="application/json")
@RequestMapping(produces="application/json")
public class StockScreenerController{
	
	Resource chartDataTemplate = new ClassPathResource("META-INF/query/elasticsearch/chartData.json");
	
	String indexName = "sgx_test";
	
	@RequestMapping("screener")
	public ScreenerResponse getChartHistograms() throws CapIQRequestException, ElasticSearchException{
		
		ESQuery query = new SearchQuery();
		query.setIndex(indexName);
		query.setQueryTemplate(parseQuery(chartDataTemplate, new HashMap()));
		
		ESResponse response = esExecutor.executeQuery(query);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		response.setObjectMapper(mapper);

		Aggregations aggregations = response.getAggregations();
		
		List<String> industries = buildIndustries(aggregations, "industry");
		List<String> industryGroup = buildIndustries(aggregations, "industryGroup");
		
		ScreenerResponse screener = new ScreenerResponse();
		screener.setIndustries(industries);
		screener.setIndustyGroups(industryGroup);
		screener.setHistograms(loadHistograms(aggregations));
		return screener;
	}

	Resource template = new ClassPathResource("META-INF/query/elasticsearch/companyInfo.json");
	
	public String parseQuery(Resource template, Map<String, Object> ctx) throws CapIQRequestException{
		String query = null;
		
		try{
			String queryTemplate = FileUtils.readFileToString(template.getFile());
			StringTemplate st = new StringTemplate(queryTemplate);
			
			Iterator<Entry<String, Object>> i = ctx.entrySet().iterator();
			
			while(i.hasNext()){
				Entry<String, Object> entry = i.next();
				st.setAttribute(entry.getKey(), entry.getValue());
			}
			
			query = st.toString();
		}
		catch(IOException e){
			throw new CapIQRequestException("IOError building input request", e);
		}
		
		return query;
	}

	@Autowired
	private ESQueryExecutor esExecutor;
	
	@RequestMapping(value="company")
	public CompanyInfo getCompany(@RequestBody IdSearch search) throws CapIQRequestException, ElasticSearchException{
		return loadCompany(search.getId());
	}
	
	private CompanyInfo loadCompany(String id) throws CapIQRequestException, ElasticSearchException{
		ESQuery query = new SearchQuery();
		query.setIndex(indexName);
		Map m = new HashMap();
		m.put("id", id);
		
		query.setQueryTemplate(parseQuery(template, m));
		
		ESResponse response = esExecutor.executeQuery(query);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		response.setObjectMapper(mapper);
		return response.getHits(CompanyInfo.class).get(0);
	}

	@RequestMapping("priceHistory")
	public PriceHistory getHistory(@RequestBody IdSearch search) throws CapIQRequestException, ElasticSearchException{
		PriceHistory ret = new PriceHistory();
		ret.setPrice(getPriceHistory(search));
		ret.setVolume(getVolumeHistory(search));
		return ret;
	}

	private List<HistoricalValue> getPriceHistory(@RequestBody IdSearch search) throws CapIQRequestException, ElasticSearchException{
		Resource histTemplate = new ClassPathResource("META-INF/query/elasticsearch/historical.json");
		ESQuery query = new SearchQuery();
		query.setIndex(indexName);
		query.setType("price");
		Map m = new HashMap();
		m.put("id", search.getId());

		query.setQueryTemplate(parseQuery(histTemplate, m));
		ESResponse response = esExecutor.executeQuery(query);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		response.setObjectMapper(mapper);
		
		return response.getHits(HistoricalValue.class);
	}

	private List<HistoricalValue> getVolumeHistory(@RequestBody IdSearch search) throws CapIQRequestException, ElasticSearchException{
		Resource histTemplate = new ClassPathResource("META-INF/query/elasticsearch/historical.json");
		ESQuery query = new SearchQuery();
		query.setIndex(indexName);
		query.setType("volume");
		Map m = new HashMap();
		m.put("id", search.getId());

		query.setQueryTemplate(parseQuery(histTemplate, m));
		ESResponse response = esExecutor.executeQuery(query);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		response.setObjectMapper(mapper);
		
		return response.getHits(HistoricalValue.class);

	}

	@RequestMapping("search")
	public SearchResults search() throws CapIQRequestException, ElasticSearchException{
		CompanyInfo info = loadCompany("A7S");
		SearchCompany res = new SearchCompany();
		res.setCode(info.getTickerCode());
		res.setCompanyName(info.getCompanyName());
		res.setDividendYield(info.getDividendYield());
		res.setMarketCap(info.getMarketCap());
		res.setIndustry(info.getIndustry());
		res.setPeRatio(info.getPeRatio());
		res.setTotalRevenue(info.getTotalRevenue());
		
		SearchResults results = new SearchResults();
		results.setCompanies(Arrays.asList(new SearchCompany[]{res}));
		return results;
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
