package com.wmsi.sgx.service.sandp.alpha;

import org.springframework.beans.factory.annotation.Autowired;

import com.wmsi.sgx.exception.ErrorBeanHelper;
import com.wmsi.sgx.model.ErrorBean;

public class AlphaFactorServiceException extends Exception{

	@Autowired
	private ErrorBeanHelper errorBeanHelper;


	private static final long serialVersionUID = 1L;

	public AlphaFactorServiceException(String msg){
		super(msg);
		errorBeanHelper.addError(new ErrorBean("AlphaFactorServiceException",
				msg, ErrorBean.ERROR, ""));
		errorBeanHelper.sendEmail();
	}
	
	public AlphaFactorServiceException(String msg, Throwable t){
		super(msg, t);
		errorBeanHelper.addError(new ErrorBean("AlphaFactorServiceException",
				msg, ErrorBean.ERROR, errorBeanHelper.getStackTrace(t)));
		errorBeanHelper.sendEmail();
	}
	
}