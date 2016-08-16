package com.wmsi.sgx.service.indexer;

import org.springframework.beans.factory.annotation.Autowired;

import com.wmsi.sgx.exception.ErrorBeanHelper;
import com.wmsi.sgx.model.ErrorBean;

public class IndexerServiceException extends Exception{

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private ErrorBeanHelper errorBeanHelper;


	public IndexerServiceException(String msg){
		super(msg);
		errorBeanHelper.addError(new ErrorBean("IndexerServiceException",
				msg, ErrorBean.ERROR, ""));
		errorBeanHelper.sendEmail();
	}
	
	
	public IndexerServiceException(String msg, Throwable t){
		super(msg, t);
		errorBeanHelper.addError(new ErrorBean("IndexerServiceException",
				msg, ErrorBean.ERROR, errorBeanHelper.getStackTrace(t)));
		errorBeanHelper.sendEmail();

	}
	
}
