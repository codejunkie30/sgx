package com.wmsi.sgx.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.financials.Financials;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.model.search.input.IdSearch;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.search.Search;

@RestController
@RequestMapping(produces="application/json")
public class CompanyController{

	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private Search<KeyDevs> keyDevsSearch;
	
	@RequestMapping(value="company")
	public Map<String, Object> getAll(@RequestBody IdSearch search) throws CompanyServiceException {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("company", getCompany(search));
		ret.put("holders", getHolders(search));
		ret.put("keyDevs", getKeyDevs(search).getKeyDevs());
		return ret;
	}
	
	@RequestMapping(value="company/info")
	public Map<String, CompanyInfo> getCompany(@RequestBody IdSearch search) throws CompanyServiceException{
		Map<String, CompanyInfo> ret = new HashMap<String, CompanyInfo>();
		ret.put("companyInfo", companyService.getById(search.getId(), CompanyInfo.class));
		return ret;
	}
	
	@RequestMapping(value="company/keyDevs")
	public KeyDevs getKeyDevs(@RequestBody IdSearch search) throws CompanyServiceException{
		return companyService.loadKeyDevs(search.getId());
	}

	@RequestMapping(value="company/holders")
	public Holders getHolders(@RequestBody IdSearch search) throws CompanyServiceException {
		Holders holders = companyService.loadHolders(search.getId());
		holders.setHolders(holders.getHolders().subList(0,  5));
		return holders;
	}
	
	@RequestMapping(value="company/financials")
	public Financials getFinancials(@RequestBody IdSearch search) throws CompanyServiceException {		
		List<CompanyFinancial> hits = companyService.loadFinancials(search.getId());
		Financials ret = new Financials();
		ret.setFinancials(hits);
		return ret;
	}

	@RequestMapping("company/priceHistory")
	public PriceHistory getHistory(@RequestBody IdSearch search) throws CompanyServiceException {
		PriceHistory ret = new PriceHistory();
		ret.setPrice(companyService.loadPriceHistory(search.getId()));
		ret.setVolume(companyService.loadVolumeHistory(search.getId()));
		return ret;
	}

	@RequestMapping(value="company/alphaFactor")
	public AlphaFactor getAlphas(@RequestBody IdSearch search) throws CompanyServiceException {		
		return companyService.loadAlphaFactors(search.getId());
	}
}
