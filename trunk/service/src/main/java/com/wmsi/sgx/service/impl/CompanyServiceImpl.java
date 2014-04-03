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
import com.wmsi.sgx.model.search.input.IdSearch;
import com.wmsi.sgx.service.CompanyService;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;
import com.wmsi.sgx.service.search.elasticsearch.query.AlphaFactorIdQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.FinancialsQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.HistoricalValueQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.RelatedCompaniesQueryBuilder;

@Service
public class CompanyServiceImpl implements CompanyService{

	@Autowired
	private SearchService companySearchService;

	@Autowired
	private ElasticSearchService elasticSearchService;

	@Override
	public CompanyInfo getById(String id, Class<CompanyInfo> clz) throws CompanyServiceException {
		try{
			return companySearchService.getById(id, clz);
		}
		catch(SearchServiceException e){
			throw new CompanyServiceException("Could not load company by id", e);
		}
	}

	@Override
	public KeyDevs loadKeyDevs(String id) throws CompanyServiceException {
		try{
			return elasticSearchService.get("sgx_test", "keyDevs", id, KeyDevs.class);
		}
		catch(ElasticSearchException e){
			throw new CompanyServiceException("Exception loading key devs", e);
		}
	}

	public Holders loadHolders(String id) throws CompanyServiceException {
		try{
			return elasticSearchService.get("sgx_test", "holders", id, Holders.class);
		}
		catch(ElasticSearchException e){
			throw new CompanyServiceException("Exception loading key devs", e);
		}
	}

	@Override
	public List<CompanyFinancial> loadFinancials(IdSearch id) throws CompanyServiceException {
		FinancialsQueryBuilder queryBuilder = new FinancialsQueryBuilder();

		try{
			return elasticSearchService.search("sgx_test", "financial", queryBuilder.build(id), CompanyFinancial.class);
		}
		catch(ElasticSearchException e){
			throw new CompanyServiceException("Exception loading price history", e);
		}
	}

	@Override
	public List<HistoricalValue> loadPriceHistory(IdSearch id) throws CompanyServiceException {
		HistoricalValueQueryBuilder queryBuilder = new HistoricalValueQueryBuilder();

		try{
			return elasticSearchService.search("sgx_test", "price", queryBuilder.build(id), HistoricalValue.class);
		}
		catch(ElasticSearchException e){
			throw new CompanyServiceException("Exception loading price history", e);
		}
	}

	@Override
	public List<HistoricalValue> loadVolumeHistory(IdSearch id) throws CompanyServiceException {
		HistoricalValueQueryBuilder queryBuilder = new HistoricalValueQueryBuilder();

		try{
			return elasticSearchService.search("sgx_test", "volume", queryBuilder.build(id), HistoricalValue.class);
		}
		catch(ElasticSearchException e){
			throw new CompanyServiceException("Exception loading price history", e);
		}
	}

	@Override
	public AlphaFactor loadAlphaFactors(String id) throws CompanyServiceException {

		List<AlphaFactor> hits = null;
		CompanyInfo info = getById(id, CompanyInfo.class);

		if(info != null){
			String gvKey = info.getGvKey().substring(3); // Trim 'GV_' prefix
															// from actual id
			AlphaFactorIdQueryBuilder builder = new AlphaFactorIdQueryBuilder();
			String query = builder.build(gvKey);

			try{
				hits = elasticSearchService.search("sgx_test", "alphaFactor", query, AlphaFactor.class);
			}
			catch(ElasticSearchException e){
				throw new CompanyServiceException("Exception alpha factors for company", e);
			}
		}

		return hits != null && hits.size() > 0 ? hits.get(0) : null;
	}

	@Override
	public List<CompanyInfo> loadRelatedCompanies(String id) throws CompanyServiceException {

		CompanyInfo company = getById(id, CompanyInfo.class);

		RelatedCompaniesQueryBuilder builder = new RelatedCompaniesQueryBuilder();
		String query = builder.build(company);

		try{
			return elasticSearchService.search("sgx_test", "company", query, CompanyInfo.class);
		}
		catch(ElasticSearchException e){
			throw new CompanyServiceException("Could not load related companies", e);
		}
	}
}
