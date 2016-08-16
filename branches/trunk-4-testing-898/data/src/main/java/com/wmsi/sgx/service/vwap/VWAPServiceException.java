package com.wmsi.sgx.service.vwap;

import org.springframework.beans.factory.annotation.Autowired;

import com.wmsi.sgx.exception.ErrorBeanHelper;
import com.wmsi.sgx.model.ErrorBean;

public class VWAPServiceException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private ErrorBeanHelper errorBeanHelper;

	
	public VWAPServiceException(String msg){
		super(msg);
		errorBeanHelper.addError(new ErrorBean("VWAPServiceException",
				msg, ErrorBean.ERROR,""));
		errorBeanHelper.sendEmail();
	}
	public VWAPServiceException(String msg, Throwable t){
		super(msg, t);
		errorBeanHelper.addError(new ErrorBean("VWAPServiceException",
				msg, ErrorBean.ERROR, errorBeanHelper.getStackTrace(t)));
		errorBeanHelper.sendEmail();
	}
}