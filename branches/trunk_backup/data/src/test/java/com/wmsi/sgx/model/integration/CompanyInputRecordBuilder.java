// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package com.wmsi.sgx.model.integration;

public class CompanyInputRecordBuilder extends CompanyInputRecordBuilderBase<CompanyInputRecordBuilder>{
	public static CompanyInputRecordBuilder companyInputRecord() {
		return new CompanyInputRecordBuilder();
	}

	public CompanyInputRecordBuilder(){
		super(new CompanyInputRecord());
	}

	public CompanyInputRecord build() {
		return getInstance();
	}
}

class CompanyInputRecordBuilderBase<GeneratorT extends CompanyInputRecordBuilderBase<GeneratorT>> {
	private CompanyInputRecord instance;

	protected CompanyInputRecordBuilderBase(CompanyInputRecord aInstance){
		instance = aInstance;
	}

	protected CompanyInputRecord getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withIndexed(Boolean aValue) {
		instance.setIndexed(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withId(String aValue) {
		instance.setId(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withTicker(String aValue) {
		instance.setTicker(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withDate(String aValue) {
		instance.setDate(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withTradeName(String aValue) {
		instance.setTradeName(aValue);

		return (GeneratorT) this;
	}
}