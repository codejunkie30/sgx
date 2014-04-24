package com.wmsi.sgx.service.search.elasticsearch.impl;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.wmsi.sgx.service.search.elasticsearch.AbstractQuery;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;


public class SourceQuery extends AbstractQuery{

	public SourceQuery(){}
	public SourceQuery(String i){id = i;}

	private String id;
	public String getId(String id){return id;}
	public void setId(String i){id = i;}

	@Override
	public EndPoint getEndPoint(){return EndPoint.SOURCE;}
	
	@Override
	public URI getURI() throws ElasticSearchException{
		
		if(StringUtils.isEmpty(getIndex()) || StringUtils.isEmpty(getType()) ||	StringUtils.isEmpty(id))
			throw new ElasticSearchException("Missing required parameters for _source query");
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/{index}/{type}/{id}/{endPoint}");		
		return builder.buildAndExpand(getIndex(), getType(), id, getEndPoint()).toUri();		
	}
	
}
