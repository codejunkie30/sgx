package com.wmsi.sgx.service.search.elasticsearch;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;


public class SourceQuery extends ESQuery{

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
