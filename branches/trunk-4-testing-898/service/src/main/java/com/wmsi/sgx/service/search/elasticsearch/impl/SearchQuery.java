package com.wmsi.sgx.service.search.elasticsearch.impl;

import com.wmsi.sgx.service.search.elasticsearch.AbstractQuery;



public class SearchQuery extends AbstractQuery{

	@Override
	public EndPoint getEndPoint(){return EndPoint.SEARCH;}	
}
