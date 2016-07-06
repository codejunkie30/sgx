package com.wmsi.sgx.service.indexer;

public class IndexerServiceException extends Exception{

	private static final long serialVersionUID = 1L;

	public IndexerServiceException(String msg){
		super(msg);
	}
	
	
	public IndexerServiceException(String msg, Throwable t){
		super(msg, t);
	}
	
}
