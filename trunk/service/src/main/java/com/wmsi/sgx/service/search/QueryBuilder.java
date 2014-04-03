package com.wmsi.sgx.service.search;

import org.elasticsearch.search.builder.SearchSourceBuilder;


public interface QueryBuilder<T>{

	SearchSourceBuilder getBuilder(T request);

	String build(T request);
}
