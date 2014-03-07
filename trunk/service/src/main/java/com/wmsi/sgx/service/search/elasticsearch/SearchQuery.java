package com.wmsi.sgx.service.search.elasticsearch;

public class SearchQuery extends ESQuery{

	@Override
	public EndPoint getEndPoint(){return EndPoint.SEARCH;}
}
