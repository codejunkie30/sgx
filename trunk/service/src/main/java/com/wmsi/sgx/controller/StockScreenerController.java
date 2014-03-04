package com.wmsi.sgx.controller;

import java.util.Arrays;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.ScreenerResponse;
import com.wmsi.sgx.model.histogram.CompanyHistogram;
import com.wmsi.sgx.model.histogram.Histogram;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchResults;

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

	@RequestMapping("query")
	public SearchResults search(){
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

		SearchResults results = new SearchResults();
		results.setCompanies(Arrays.asList(new SearchCompany[]{res, res2}));
		
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
