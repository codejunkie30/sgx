package com.wmsi.sgx.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@Service
public class CompanyServiceImpl implements CompanyService{

	@Autowired
	private SearchService<Company> companySearchService;

	@Override
	@Cacheable(value = "company")
	public Company getById(String id) throws CompanyServiceException {
		try{
			return companySearchService.getById(id, Company.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Could not load company by id", e);
		}
	}

	@Autowired
	private SearchService<String> keyDevsSearch;
	
	@Override
	@Cacheable(value = "keyDevs")
	public KeyDevs loadKeyDevs(String id) throws CompanyServiceException {
		try{
			return keyDevsSearch.getById(id, KeyDevs.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Exception loading key devs", e);
		}
	}

	@Autowired
	private SearchService<String> holdersSearch;

	@Override
	@Cacheable(value = "holders")
	public Holders loadHolders(String id) throws CompanyServiceException {
		try{
			return holdersSearch.getById(id, Holders.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Exception loading key devs", e);
		}
	}

	@Autowired
	private SearchService<String> financialSearch;
	
	@Override
	@Cacheable(value = "financials")
	public List<Financial> loadFinancials(String id) throws CompanyServiceException {
		try{
			return financialSearch.search(id, Financial.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Exception loading price history", e);
		}
	}
	
	@Autowired
	private SearchService<String> priceHistorySearch;

	@Override
	@Cacheable(value = "priceHistory")
	public List<HistoricalValue> loadPriceHistory(String id) throws CompanyServiceException {
		try{
			return priceHistorySearch.search(id, HistoricalValue.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Exception loading price history", e);
		}
	}

	@Autowired
	private SearchService<String> volumeHistorySearch;

	@Override
	@Cacheable(value = "volumeHistory")
	public List<HistoricalValue> loadVolumeHistory(String id) throws CompanyServiceException {
		try{
			return priceHistorySearch.search(id, HistoricalValue.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Exception loading price history", e);
		}
	}

	@Autowired
	private SearchService<String> alphaFactorIdSearch;
	
	@Override
	@Cacheable(value = "alphaFactor")
	public AlphaFactor loadAlphaFactors(String id) throws CompanyServiceException {

		List<AlphaFactor> hits = null;
		Company info = getById(id);

		String gvKey = info.getGvKey();
				
		if(info != null && !StringUtils.isEmpty(gvKey)){
			gvKey = info.getGvKey().substring(3); // Trim 'GV_' prefix from actual id

			try{
				hits = alphaFactorIdSearch.search(gvKey, AlphaFactor.class);
			}
			catch(SearchServiceException e){
				throw new CompanyServiceException("Exception alpha factors for company", e);
			}
		}

		return hits != null && hits.size() > 0 ? hits.get(0) : null;
	}

	@Autowired
	private SearchService<Company> relatedCompaniesSearch;

	@Override
	@Cacheable(value = "relatedCompanies")
	public List<Company> loadRelatedCompanies(String id) throws CompanyServiceException{

		try{
			Company company = getById(id);
			return relatedCompaniesSearch.search(company, Company.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Could not load related companies", e);
		}		
	}
}
