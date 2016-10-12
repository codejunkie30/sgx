package com.wmsi.sgx.service.search.elasticsearch.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.distribution.DistributionRequestField;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.service.search.StatAggregation;
import com.wmsi.sgx.util.Util;

/**
 * 
 * This class is used to build queries related to distributions.
 *
 */
public class DistributionsQueryBuilder extends AbstractQueryBuilder{

	private List<String> fields;
	private Map<String, StatAggregation> ranges;
	private AccountType accType;
	
	@Value("${list.permitted.exchanges}")
	private String permittedExchangesList="SGX,CATALIST";
	
	public DistributionsQueryBuilder(List<DistributionRequestField> f, Map<String, StatAggregation> r, AccountType accType){

		ranges = r;
		fields = new ArrayList<String>();
		this.accType = accType;
		
		for(DistributionRequestField req : f){
			fields.add(req.getField());
		}
	}

	/**
	 * Builds query.
	 * 
	 * @return String
	 */
	@Override
	public String build() {
		List<String> exchangesWhiteList = new ArrayList<String>();
		for(int i=0; i<permittedExchangesList.split(",").length; i++){
			exchangesWhiteList.add(permittedExchangesList.split(",")[i]);
		}
		
		SearchSourceBuilder query;
		if(accType.equals(AccountType.PREMIUM) || accType.equals(AccountType.TRIAL) || accType.equals(AccountType.ADMIN) || accType.equals(AccountType.MASTER) ){
			query = new SearchSourceBuilder()
					.query(QueryBuilders.constantScoreQuery(FilterBuilders.matchAllFilter()))
					.fetchSource(fields.toArray(new String[0]), null)
					.size(MAX_RESULTS);
			
		}else{
			query = new SearchSourceBuilder().query(QueryBuilders.constantScoreQuery(FilterBuilders.boolFilter().must(FilterBuilders.termsFilter("exchange", exchangesWhiteList))))
					.fetchSource(fields.toArray(new String[0]), null)
					.size(MAX_RESULTS);
		}

		for(String field : fields){

			if(!Util.isNumberField(SearchCompany.class, field)){
				query.aggregation(AggregationBuilders.terms(field).field(field).size(2000));
			}
			else if(ranges != null && ranges.size() > 0){
				StatAggregation stat = ranges.get(field);
				
				AggregationBuilder<?> range = buildRangeAggregation(field, stat);

				if(range != null){
					query.aggregation(range);
				}
			}
		}

		return query.toString();
	}

	/**
	 * Builds Aggregation Range builder.
	 * 
	 * @param field
	 *            String
	 * @param stat
	 *            StatAggregation
	 * @return AggregationBuilder<?>
	 */
	private AggregationBuilder<?> buildRangeAggregation(String field, StatAggregation stat) {
		
		double interval = calculateInterval(stat);
		double min = stat.getMin();
		double max = stat.getMax();

		if(interval <= 0)
			return null;
		
		RangeBuilder range = AggregationBuilders.range(field).field(field);

		for(double i = min; i < max;){
			
			double to = i + interval;
			
			if(to > max){			
				// Hack to make 'to' inclusive for last range so max value is returned.
				// Elasticsearch is as of version 1.1 sorely missing a way to do this with aggregations
				// other than using unbounded 'from' which won't return the max value in the results.
				to = max + 0.000000001;
			}
			
			range.addRange(i, to);
			
			i = to;
		}

		return getAggregationFilter(range, field);

	}

	/**
	 * Builds FilterBuilder for Aggregations.
	 * 
	 * @param agg
	 *            AggregationBuilder<?>
	 * @param field
	 *            String
	 * @return AggregationBuilder<?>
	 */
	private AggregationBuilder<?> getAggregationFilter(AggregationBuilder<?> agg, String field) {

		AggregationBuilder<?> builder = agg;

		if(field.equals("avgBrokerReq")){
			FilterBuilder filter = FilterBuilders.rangeFilter("targetPriceNum").from(3);

			builder = AggregationBuilders.filter(field).filter(filter).subAggregation(agg);
		}

		return builder;
	}

	/**
	 * Calculates the interval
	 * @param stat StatAggregation
	 * @return Double
	 */
	private Double calculateInterval(StatAggregation stat) {
		double total = stat.getMax() - stat.getMin();
		double sqrt = Math.sqrt(stat.getCount());

		double ret = 0.0;

		if(total != 0)
			ret = new BigDecimal(total / sqrt).doubleValue();

		return ret;
	}

}
