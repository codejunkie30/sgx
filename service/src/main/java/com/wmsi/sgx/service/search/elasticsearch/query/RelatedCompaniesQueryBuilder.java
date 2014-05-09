package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.Company;

public class RelatedCompaniesQueryBuilder extends AbstractQueryBuilder{
	
	private Company company;

	public RelatedCompaniesQueryBuilder(Company company) {
		this.company = company;
	}
	
	@Override
	public String build(){
		Double mk = company.getMarketCap();
		
		return new SearchSourceBuilder()
			.query(QueryBuilders
				.functionScoreQuery(
					FilterBuilders.andFilter()
					.add(FilterBuilders
						.existsFilter("marketCap"))
					.add(FilterBuilders
						.boolFilter()
							.mustNot(FilterBuilders.termFilter("tickerCode", company.getTickerCode()))
							.must(FilterBuilders.termFilter("industry", company.getIndustry()))
							.must(FilterBuilders.termFilter("industryGroup", company.getIndustryGroup()))
					))							
				.add(ScoreFunctionBuilders
					.linearDecayFunction("marketCap", mk, mk)))
			.toString();
	}	
}
