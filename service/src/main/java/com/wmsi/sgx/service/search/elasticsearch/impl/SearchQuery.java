package com.wmsi.sgx.service.search.elasticsearch.impl;

import com.wmsi.sgx.service.search.elasticsearch.AbstractQuery;


/**
 * 
 * This abstract class is used to get the end point url of the search.
 *
 */
public class SearchQuery extends AbstractQuery{

	@Override
	public EndPoint getEndPoint(){return EndPoint.SEARCH;}	
}
