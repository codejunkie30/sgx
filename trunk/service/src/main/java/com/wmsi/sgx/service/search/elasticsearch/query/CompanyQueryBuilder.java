package com.wmsi.sgx.service.search.elasticsearch.query;

import java.util.List;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.search.Criteria;

public class CompanyQueryBuilder extends AbstractQueryBuilder{

	private List<Criteria> criteria;	
	
	public CompanyQueryBuilder (List<Criteria> criteria) {
		this.criteria = criteria;		
	}
	
	@Override
	public String build(){

		// Match all if no criteria or if only criteria is percentChange
		if(criteria == null || criteria.size() <= 0 || 
				(criteria.size() == 1 && criteria.get(0).getField().equals("percentChange")) )
			
			return new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()).size(MAX_RESULTS).toString();
		
		BoolFilterBuilder boolFilter = FilterBuilders.boolFilter();		
		SearchSourceBuilder builder = new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
				boolFilter
				.cache(true)
				))
		.size(MAX_RESULTS);
		
		for(Criteria c : criteria){
			
			FilterBuilder filter = null;
			
			if(c.getField().equals("percentChange")){
				// Skip, use com.wmsi.service.search.filter.PercentChangeFilter
				continue;
			}
			else if(c.getTo() != null || c.getFrom() != null){
				filter = buildRangeFilter(c);
			}
			else{
				filter = buildTermFilter(c);
			}
			
			boolFilter.must(filter);
		}
		
		return builder.toString();
	}
	
	private FilterBuilder buildRangeFilter(Criteria c){
		RangeFilterBuilder rangeBuilder = FilterBuilders.rangeFilter(c.getField());
		
		if(c.getFrom() != null)
			rangeBuilder.from(c.getFrom());

		if(c.getTo() != null)
			rangeBuilder.to(c.getTo());
		
		return rangeBuilder;
	}

	private FilterBuilder buildTermFilter(Criteria c){
		return FilterBuilders.termFilter(c.getField(), c.getValue());
	}
}
