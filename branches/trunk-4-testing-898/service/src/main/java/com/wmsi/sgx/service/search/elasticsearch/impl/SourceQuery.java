package com.wmsi.sgx.service.search.elasticsearch.impl;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.wmsi.sgx.service.search.elasticsearch.AbstractQuery;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;

/**
 * 
 * This class is used for getting the end point url.
 *
 */
public class SourceQuery extends AbstractQuery{
	
	private Logger log = LoggerFactory.getLogger(SourceQuery.class);
	public SourceQuery(){}
	public SourceQuery(String i){id = i;}

	private String id;
	public void setId(String i){id = i;}

	@Override
	public EndPoint getEndPoint(){return EndPoint.SOURCE;}
	
	/**
	 * Retrieves the end point uri for elastic search.
	 * 
	 * @return URI
	 * @throws ElasticSearchException
	 */
	@Override
	public URI getURI() throws ElasticSearchException{
		log.debug("StringUtils.isEmpty(getIndex()): {}  , StringUtils.isEmpty(getType()): {},StringUtils.isEmpty(id):{} ", 
				StringUtils.isEmpty(getIndex()), StringUtils.isEmpty(getType()), StringUtils.isEmpty(id));
		if(StringUtils.isEmpty(getIndex()) || StringUtils.isEmpty(getType()) ||	StringUtils.isEmpty(id))
			throw new ElasticSearchException("Missing required parameters for _source query");
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/{index}/{type}/{id}/{endPoint}");		
		return builder.buildAndExpand(getIndex(), getType(), id, getEndPoint()).toUri();		
	}
	
}
