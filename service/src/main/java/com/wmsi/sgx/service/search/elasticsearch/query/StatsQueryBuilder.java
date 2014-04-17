package com.wmsi.sgx.service.search.elasticsearch.query;

import java.util.List;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.distribution.DistributionRequestField;

public class StatsQueryBuilder extends AbstractQueryBuilder<List<DistributionRequestField>>{

	private static final int MAX_RESULTS = 2000;
	
	@Override
	public SearchSourceBuilder getBuilder(List<DistributionRequestField> fields) {
		
		SearchSourceBuilder query = new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
				FilterBuilders.matchAllFilter()))
			.fetchSource(false)
			.size(MAX_RESULTS);

		// Add aggregations
		for(DistributionRequestField req : fields){
			
			if(!req.getField().equals("industry") && !req.getField().equals("industryGroup")){
			
				String field = req.getField();
				
				query.aggregation(AggregationBuilders
					.stats(field)
					.field(field)				
				);
			}
		}

		return query;
	}
}
