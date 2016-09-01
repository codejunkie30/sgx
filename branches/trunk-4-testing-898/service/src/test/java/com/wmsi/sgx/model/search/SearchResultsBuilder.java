// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package com.wmsi.sgx.model.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsBuilder extends SearchResultsBuilderBase<SearchResultsBuilder>{
	public static SearchResultsBuilder searchResults() {
		return new SearchResultsBuilder();
	}

	public SearchResultsBuilder(){
		super(new SearchResults());
	}

	public SearchResults build() {
		return getInstance();
	}
}

class SearchResultsBuilderBase<GeneratorT extends SearchResultsBuilderBase<GeneratorT>> {
	private SearchResults instance;

	protected SearchResultsBuilderBase(SearchResults aInstance){
		instance = aInstance;
	}

	protected SearchResults getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withCompanies(List<SearchCompany> aValue) {
		instance.setCompanies(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withAddedCompany(SearchCompany aValue) {
		if(instance.getCompanies() == null){
			instance.setCompanies(new ArrayList<SearchCompany>());
		}

		((ArrayList<SearchCompany>) instance.getCompanies()).add(aValue);

		return (GeneratorT) this;
	}

	public AddedCompanySearchCompanyBuilder withAddedCompany() {
		SearchCompany obj = new SearchCompany();

		withAddedCompany(obj);

		return new AddedCompanySearchCompanyBuilder(obj);
	}

	public class AddedCompanySearchCompanyBuilder extends SearchCompanyBuilderBase<AddedCompanySearchCompanyBuilder>{
		public AddedCompanySearchCompanyBuilder(SearchCompany aInstance){
			super(aInstance);
		}

		@SuppressWarnings("unchecked")
		public GeneratorT endCompany() {
			return (GeneratorT) SearchResultsBuilderBase.this;
		}
	}

	public static class SearchCompanyBuilderBase<GeneratorT extends SearchCompanyBuilderBase<GeneratorT>> {
		private SearchCompany instance;

		protected SearchCompanyBuilderBase(SearchCompany aInstance){
			instance = aInstance;
		}

		protected SearchCompany getInstance() {
			return instance;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withAvgBrokerReq(Double aValue) {
			instance.setAvgBrokerReq(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withBeta5Yr(Double aValue) {
			instance.setBeta5Yr(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withCompanyName(String aValue) {
			instance.setCompanyName(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withDividendYield(Double aValue) {
			instance.setDividendYield(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withEbitdaMargin(Double aValue) {
			instance.setEbitdaMargin(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withEps(Double aValue) {
			instance.setEps(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withIndustry(String aValue) {
			instance.setIndustry(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withIndustryGroup(String aValue) {
			instance.setIndustryGroup(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withMarketCap(Double aValue) {
			instance.setMarketCap(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withNetProfitMargin(Double aValue) {
			instance.setNetProfitMargin(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withPeRatio(Double aValue) {
			instance.setPeRatio(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withPercentChange(Double aValue) {
			instance.setPercentChange(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withPriceToBookRatio(Double aValue) {
			instance.setPriceToBookRatio(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withPriceVs52WeekHigh(Double aValue) {
			instance.setPriceVs52WeekHigh(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withPriceVs52WeekLow(Double aValue) {
			instance.setPriceVs52WeekLow(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withTargetPriceNum(Double aValue) {
			instance.setTargetPriceNum(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withTickerCode(String aValue) {
			instance.setTickerCode(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withTotalDebtEquity(Double aValue) {
			instance.setTotalDebtEquity(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withTotalRev1YrAnnGrowth(Double aValue) {
			instance.setTotalRev1YrAnnGrowth(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withTotalRev3YrAnnGrowth(Double aValue) {
			instance.setTotalRev3YrAnnGrowth(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withTotalRev5YrAnnGrowth(Double aValue) {
			instance.setTotalRev5YrAnnGrowth(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withTotalRevenue(Double aValue) {
			instance.setTotalRevenue(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withVolume(Double aValue) {
			instance.setVolume(aValue);

			return (GeneratorT) this;
		}
	}
}