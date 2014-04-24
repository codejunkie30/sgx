package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.Company;

public class RelatedCompaniesQueryBuilder extends AbstractQueryBuilder{
	
	private Company company;

	public RelatedCompaniesQueryBuilder(Company company) {
		this.company = company;
	}
	
	@Override
	public String build(){
		return new SearchSourceBuilder()
			.query(QueryBuilders.boolQuery()
					.mustNot(QueryBuilders.termQuery("tickerCode", company.getTickerCode()))
					.should(QueryBuilders.termQuery("industry", company.getIndustry()))
					.should(QueryBuilders.termQuery("industryGroup", company.getIndustryGroup())))
			.postFilter(FilterBuilders
					.scriptFilter(SCRIPT)
					.addParam("f", "marketCap")
					.addParam("fv",  company.getMarketCap())
					.addParam("pct", 0.2)
					)
			.size(MAX_RESULTS)
			.toString();
	}
	
	private static final String SCRIPT =
		"!doc[f].isEmpty() && doc[f].value > fv - (fv * pct) && doc[f].value < fv + (fv * pct)";			
}
