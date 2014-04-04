package com.wmsi.sgx.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@Service
public class CompanyServiceImpl implements CompanyService{

	@Autowired
	private SearchService<CompanyInfo> companySearchService;

	@Override
	public CompanyInfo getById(String id, Class<CompanyInfo> clz) throws CompanyServiceException {
		try{
			return companySearchService.getById(id, clz);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Could not load company by id", e);
		}
	}

	@Autowired
	private SearchService<String> keyDevsSearch;
	
	@Override
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
	public List<CompanyFinancial> loadFinancials(String id) throws CompanyServiceException {
		try{
			return financialSearch.search(id, CompanyFinancial.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Exception loading price history", e);
		}
	}
	
	@Autowired
	private SearchService<String> priceHistorySearch;

	@Override
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
	public AlphaFactor loadAlphaFactors(String id) throws CompanyServiceException {

		List<AlphaFactor> hits = null;
		CompanyInfo info = getById(id, CompanyInfo.class);

		if(info != null){
			String gvKey = info.getGvKey().substring(3); // Trim 'GV_' prefix from actual id

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
	private SearchService<CompanyInfo> relatedCompaniesSearch;

	@Override
	public List<CompanyInfo> loadRelatedCompanies(String id) throws CompanyServiceException{

		try{
			CompanyInfo company = getById(id, CompanyInfo.class);
			return relatedCompaniesSearch.search(company, CompanyInfo.class);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Could not load related companies", e);
		}		
	}
}
