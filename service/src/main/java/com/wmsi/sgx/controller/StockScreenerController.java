package com.wmsi.sgx.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.financials.Financials;
import com.wmsi.sgx.model.histogram.CompanyHistogram;
import com.wmsi.sgx.model.histogram.Histogram;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.model.screener.ScreenerResponse;
import com.wmsi.sgx.model.search.input.IdSearch;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.search.elasticsearch.Aggregation;
import com.wmsi.sgx.service.search.elasticsearch.Aggregations;
import com.wmsi.sgx.service.search.elasticsearch.Bucket;
import com.wmsi.sgx.service.search.elasticsearch.ESQuery;
import com.wmsi.sgx.service.search.elasticsearch.ESQueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.ESResponse;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;
import com.wmsi.sgx.service.search.elasticsearch.SearchQuery;

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
	
	@RequestMapping(value="company")
	public Map<String, Object> getAll(@RequestBody IdSearch search) throws  ElasticSearchException, IOException{
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("company", loadCompany(search.getId()));
		ret.put("holders", loadHolders(search.getId()).getHolders().subList(0,  5));
		ret.put("keyDevs", loadDevs(search.getId()).getKeyDevs());
		
		return ret;
	}
	
	@RequestMapping(value="company/info")
	public Map<String, CompanyInfo> getCompany(@RequestBody IdSearch search) throws ElasticSearchException, IOException{
		Map<String, CompanyInfo> ret = new HashMap<String, CompanyInfo>();
		ret.put("companyInfo", loadCompany(search.getId()));
		return ret;
	}
	
	private CompanyInfo loadCompany(String id) throws ElasticSearchException, IOException{
		String template = getQuery(new ClassPathResource("META-INF/query/elasticsearch/companyInfo.json"));
		List<CompanyInfo> info = elasticSearchService.search(indexName, template, getParms(id), CompanyInfo.class);
		return info != null && info.size() > 0 ? info.get(0) : null;
	}
	
	@RequestMapping(value="company/financials")
	public Financials getFinancials(@RequestBody IdSearch search) throws ElasticSearchException, IOException{
		return loadFinancials(search.getId());
	}

	private Financials loadFinancials(String id) throws ElasticSearchException, IOException{
		String template = getQuery(new ClassPathResource("META-INF/query/elasticsearch/financials.json"));
		List<CompanyFinancial> hits = elasticSearchService.search(indexName, template, getParms(id), CompanyFinancial.class);
		Financials ret = new Financials();
		ret.setFinancials(hits);
		return ret;
	}

	@RequestMapping(value="company/holders")
	public Holders getHolders(@RequestBody IdSearch search) throws ElasticSearchException, IOException{
		Holders holders = loadHolders(search.getId());
		holders.setHolders(holders.getHolders().subList(0,  5));
		return holders;
	}

	private Holders loadHolders(String id) throws ElasticSearchException, IOException{
		String template = getQuery(new ClassPathResource("META-INF/query/elasticsearch/holders.json"));
		List<Holders> hits = elasticSearchService.search(indexName, template, getParms(id), Holders.class);
		return hits != null && hits.size() > 0 ? hits.get(0) : null;
	}

	@RequestMapping(value="company/keyDevs")
	public KeyDevs getDevs(@RequestBody IdSearch search) throws ElasticSearchException, IOException{
		return loadDevs(search.getId());
	}

	private KeyDevs loadDevs(String id) throws ElasticSearchException, IOException{
		String template = getQuery(new ClassPathResource("META-INF/query/elasticsearch/keyDevs.json"));
		List<KeyDevs> hits = elasticSearchService.search(indexName, template, getParms(id), KeyDevs.class);
		return hits != null && hits.size() > 0 ? hits.get(0) : null;
	}

	@RequestMapping(value="company/alphaFactor")
	public AlphaFactor getAlphas(@RequestBody IdSearch search) throws ElasticSearchException, IOException{
		CompanyInfo company = loadCompany(search.getId());
		return loadAlphas(company.getGvKey().substring(3));
	}

	private AlphaFactor loadAlphas(String id) throws ElasticSearchException, IOException{
		String template = getQuery(new ClassPathResource("META-INF/query/elasticsearch/alphaFactor.json"));
		List<AlphaFactor> hits = elasticSearchService.search(indexName, template, getParms(id), AlphaFactor.class);
		return hits != null && hits.size() > 0 ? hits.get(0) : null;
	}

	@RequestMapping("company/priceHistory")
	public PriceHistory getHistory(@RequestBody IdSearch search) throws ElasticSearchException, IOException{
		PriceHistory ret = new PriceHistory();
		ret.setPrice(getPriceHistory(search));
		ret.setVolume(getVolumeHistory(search));
		return ret;
	}

	private List<HistoricalValue> getPriceHistory(@RequestBody IdSearch search) throws ElasticSearchException, IOException{
		String template = getQuery(new ClassPathResource("META-INF/query/elasticsearch/historical.json"));
		return elasticSearchService.search(indexName, "price", template, getParms(search.getId()), HistoricalValue.class);
	}

	private List<HistoricalValue> getVolumeHistory(@RequestBody IdSearch search) throws ElasticSearchException, IOException{
		String template = getQuery(new ClassPathResource("META-INF/query/elasticsearch/historical.json"));
		return elasticSearchService.search(indexName, "volume", template, getParms(search.getId()), HistoricalValue.class);
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
	
	private String getQuery(Resource template) throws IOException{
		return FileUtils.readFileToString(template.getFile());
	}
	
	private Map<String, Object> getParms(String id){
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("id", id);
		return parms;
	}

}
