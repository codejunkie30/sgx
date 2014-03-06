package com.wmsi.sgx.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.ScreenerResponse;
import com.wmsi.sgx.model.histogram.CompanyHistogram;
import com.wmsi.sgx.model.histogram.Histogram;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.search.elasticsearch.ESQuery;
import com.wmsi.sgx.service.search.elasticsearch.ESQueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.ESResponse;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;

@RestController
@RequestMapping(value="search", produces="application/json")
public class StockScreenerController{
	
	@RequestMapping("screener")
	public ScreenerResponse getChartHistograms(){
		ScreenerResponse response = new ScreenerResponse();
		response.setHistograms(loadHistograms());
		response.setIndustries(Arrays.asList(new String[]{"Bacon", "Pooridge Testing", "Horse Cosmetics", "Music for fishes"}));
		return response;
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
	
	private CompanyInfo getCompany() throws CapIQRequestException, ElasticSearchException{
		ESQuery query = new ESQuery();
		Map m = new HashMap();
		m.put("id", "A7S");
		
		query.setQueryTemplate(parseQuery(template, m));
		
		ESResponse response = esExecutor.executeQuery(query);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		response.setObjectMapper(mapper);
		return response.getHits(CompanyInfo.class).get(0);
		
		
	}
		
	@RequestMapping("query")
	public SearchResults search() throws CapIQRequestException, ElasticSearchException{
		CompanyInfo info = getCompany();
		SearchCompany res = new SearchCompany();
		res.setCode(info.getTickerCode());
		res.setCompanyName(info.getCompanyName());
		res.setDividendYield(info.getDividendYield());
		res.setMarketCap(info.getMarketCap());
		res.setIndustry(info.getIndustry());
		res.setPeRatio(info.getPeRatio());
		res.setTotalRevenue(info.getTotalRevenue());

		/*
		SearchCompany res = new SearchCompany();
		res.setCode("C6L");
		res.setCompanyName("Singapore Airlines Ltd.");
		res.setDividendYield(2.6418);
		res.setMarketCap(12031.992878);
		res.setIndustry("Airlines");
		res.setPeRatio(19.548585992072425D);
		res.setTotalRevenue(15282.3D);

		SearchCompany res2 = new SearchCompany();
		res2.setCode("EB5");
		res2.setCompanyName("First Resources Ltd.");
		res2.setDividendYield(2.0361);
		res2.setMarketCap(3500.801261);
		res2.setIndustry("Food Products");
		res2.setPeRatio(11.821560405564359);
		res2.setTotalRevenue(626.498);
		*/
		SearchResults results = new SearchResults();
		//results.setCompanies(Arrays.asList(new SearchCompany[]{res, res2}));
		results.setCompanies(Arrays.asList(new SearchCompany[]{res}));
		
		return results;
	}
	
	private CompanyHistogram loadHistograms(){
		Histogram a = new Histogram();
		a.setCount(6L);
		a.setKey(1000L);

		Histogram b = new Histogram();
		b.setCount(2L);
		b.setKey(2000L);

		Histogram c = new Histogram();
		c.setCount(14L);
		c.setKey(3000L);

		CompanyHistogram ch = new CompanyHistogram();
		ch.setMarketCap(Arrays.asList(new Histogram[]{a,b,c}));
		ch.setDividendYield(Arrays.asList(new Histogram[]{a,b,c}));
		ch.setPeRatio(Arrays.asList(new Histogram[]{a,b,c}));
		ch.setTotalRevenue(Arrays.asList(new Histogram[]{a,b,c}));
		
		return ch;
	}
}
