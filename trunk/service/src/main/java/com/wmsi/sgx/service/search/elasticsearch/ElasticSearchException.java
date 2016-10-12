package com.wmsi.sgx.service.search.elasticsearch;

/**
 * 
 * This class holds the exception obtained during elastic search
 *
 */
public class ElasticSearchException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public ElasticSearchException(String msg){
		super(msg);
	}
	
	public ElasticSearchException(String msg, Throwable t){
		super(msg, t);
	}
}
