// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package com.wmsi.sgx.service.search;

import java.util.ArrayList;
import java.util.List;

public class AggregationsBuilder extends AggregationsBuilderBase<AggregationsBuilder>{
	public static AggregationsBuilder aggregations() {
		return new AggregationsBuilder();
	}

	public AggregationsBuilder(){
		super(new Aggregations());
	}

	public Aggregations build() {
		return getInstance();
	}
}

class AggregationsBuilderBase<GeneratorT extends AggregationsBuilderBase<GeneratorT>> {
	private Aggregations instance;

	protected AggregationsBuilderBase(Aggregations aInstance){
		instance = aInstance;
	}

	protected Aggregations getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withAggregations(List<Aggregation> aValue) {
		instance.setAggregations(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withAddedAggregation(Aggregation aValue) {
		if(instance.getAggregations() == null){
			instance.setAggregations(new ArrayList<Aggregation>());
		}

		((ArrayList<Aggregation>) instance.getAggregations()).add(aValue);

		return (GeneratorT) this;
	}

	public AddedAggregationAggregationBuilder withAddedAggregation() {
		Aggregation obj = new Aggregation();

		withAddedAggregation(obj);

		return new AddedAggregationAggregationBuilder(obj);
	}

	public class AddedAggregationAggregationBuilder extends AggregationBuilderBase<AddedAggregationAggregationBuilder>{
		public AddedAggregationAggregationBuilder(Aggregation aInstance){
			super(aInstance);
		}

		@SuppressWarnings("unchecked")
		public GeneratorT endAggregation() {
			return (GeneratorT) AggregationsBuilderBase.this;
		}
	}

	public static class AggregationBuilderBase<GeneratorT extends AggregationBuilderBase<GeneratorT>> {
		private Aggregation instance;

		protected AggregationBuilderBase(Aggregation aInstance){
			instance = aInstance;
		}

		protected Aggregation getInstance() {
			return instance;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withName(String aValue) {
			instance.setName(aValue);

			return (GeneratorT) this;
		}
	}
}
