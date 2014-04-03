package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.wmsi.sgx.model.search.input.IdSearch;

public class FinancialsQueryBuilder extends AbstractQueryBuilder<IdSearch>{

	@Override
	public SearchSourceBuilder getBuilder(IdSearch id) {
		
		return new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
					FilterBuilders.boolFilter()
						.must(FilterBuilders.termFilter("tickerCode", id.getId()))
						.must(FilterBuilders.typeFilter("financial"))))
			.size(2000)
			.sort("period", SortOrder.ASC);
	}
}
