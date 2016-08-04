// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package com.wmsi.sgx.model.distribution;

public class DistributionBucketBuilder extends DistributionBucketBuilderBase<DistributionBucketBuilder>{
	public static DistributionBucketBuilder distributionBucket() {
		return new DistributionBucketBuilder();
	}

	public DistributionBucketBuilder(){
		super(new DistributionBucket());
	}

	public DistributionBucket build() {
		return getInstance();
	}
}

class DistributionBucketBuilderBase<GeneratorT extends DistributionBucketBuilderBase<GeneratorT>> {
	private DistributionBucket instance;

	protected DistributionBucketBuilderBase(DistributionBucket aInstance){
		instance = aInstance;
	}

	protected DistributionBucket getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withCount(Long aValue) {
		instance.setCount(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withFrom(String aValue) {
		instance.setFrom(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withKey(String aValue) {
		instance.setKey(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withTo(String aValue) {
		instance.setTo(aValue);

		return (GeneratorT) this;
	}
}