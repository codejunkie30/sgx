package com.wmsi.sgx.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.search.IdSearch;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.conversion.ModelMapper;

@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class CompanyController{

	@Autowired
	private CompanyService companyService;
	
	@Autowired 
	private ModelMapper mapper;
	
	@RequestMapping(value="company")
	public Map<String, Object> getAll(@RequestBody IdSearch search) throws CompanyServiceException {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("company", getCompany(search));
		ret.put("holders", getHolders(search));
		ret.put("keyDevs", getKeyDevs(search).getKeyDevs());
		ret.put("alphaFactors", getAlphas(search));
		return ret;
	}
	
	@RequestMapping(value="company/info")
	public Map<String, Company> getCompany(@RequestBody IdSearch search) throws CompanyServiceException{
		Map<String, Company> ret = new HashMap<String, Company>();
		ret.put("companyInfo", companyService.getById(search.getId()));
		return ret;
	}
	
	@RequestMapping(value="company/keyDevs")
	public KeyDevs getKeyDevs(@RequestBody IdSearch search) throws CompanyServiceException{
		return companyService.loadKeyDevs(search.getId());
	}

	@RequestMapping(value="company/holders")
	public Holders getHolders(@RequestBody IdSearch search) throws CompanyServiceException {
		Holders holders = companyService.loadHolders(search.getId());
		holders.setHolders(holders.getHolders().subList(0,  5)); // Only return first 5 holders
		return holders;
	}
	
	@RequestMapping(value="company/financials")
	public Financials getFinancials(@RequestBody IdSearch search) throws CompanyServiceException {		
		List<Financial> hits = companyService.loadFinancials(search.getId());
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

	@RequestMapping(value="company/relatedCompanies")
	public List<SearchCompany> getRelatedCompanies(@RequestBody IdSearch search) throws CompanyServiceException {		
		List<Company> companies = companyService.loadRelatedCompanies(search.getId());
		return mapper.mapList(companies, SearchCompany.class);
	}
}
