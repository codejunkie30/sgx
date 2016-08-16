package com.wmsi.sgx.service.sandp.capiq;

import org.springframework.beans.factory.annotation.Autowired;

import com.wmsi.sgx.exception.ErrorBeanHelper;
import com.wmsi.sgx.model.ErrorBean;

public class ResponseParserException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private ErrorBeanHelper errorBeanHelper;


	public ResponseParserException(String msg){
		super(msg);
		errorBeanHelper.addError(new ErrorBean("ResponseParserException",
				msg, ErrorBean.ERROR, ""));
		errorBeanHelper.sendEmail();
	}
	
	public ResponseParserException(String msg, Throwable t){
		super(msg, t);
		errorBeanHelper.addError(new ErrorBean("ResponseParserException",
				msg, ErrorBean.ERROR, errorBeanHelper.getStackTrace(t)));
		errorBeanHelper.sendEmail();
	}

}
