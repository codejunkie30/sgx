package com.wmsi.sgx.service.search.elasticsearch.query;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.Company;

/**
 * 
 * This class is used to build a builder for related companies.
 *
 */
public class RelatedCompaniesQueryBuilder extends AbstractQueryBuilder{
	
	private static final int MAX_COMPANIES = 10;
	
	private Company company;
	private AccountType accType;
	
	@Value("${list.permitted.exchanges}")
	private String permittedExchangesList="SGX,CATALIST";

	public RelatedCompaniesQueryBuilder(Company company) {
		this.company = company;
	}
	
	public RelatedCompaniesQueryBuilder(Company company, AccountType accType) {
		this.company = company;
		this.accType = accType;
	}
	
	/**
	 * Builds query for related companies.
	 * 
	 * @return String
	 */
	@Override
	public String build(){
		Double mk = company.getMarketCap();
		List<String> exchangesWhiteList = new ArrayList<String>();
		for(int i=0; i<permittedExchangesList.split(",").length; i++){
			exchangesWhiteList.add(permittedExchangesList.split(",")[i]);
		}
		
		if(mk == null)
			return null; // Can't compare without it. 
		if(accType.equals(AccountType.PREMIUM) || accType.equals(AccountType.TRIAL)|| accType.equals(AccountType.ADMIN) || accType.equals(AccountType.MASTER)){
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
					.size(MAX_COMPANIES)
					.toString();
		}else{
			return new SearchSourceBuilder()
					.query(QueryBuilders
						.functionScoreQuery(
							FilterBuilders.andFilter()
							.add(FilterBuilders
								.existsFilter("marketCap"))
							.add(FilterBuilders
								.boolFilter()
									.mustNot(FilterBuilders.termFilter("tickerCode", company.getTickerCode()))
									.must(FilterBuilders.termsFilter("exchange", exchangesWhiteList))
									.must(FilterBuilders.termFilter("industry", company.getIndustry()))
									.must(FilterBuilders.termFilter("industryGroup", company.getIndustryGroup()))
							))							
						.add(ScoreFunctionBuilders
							.linearDecayFunction("marketCap", mk, mk)))
					.size(MAX_COMPANIES)
					.toString();
		}
		
	}	
}
