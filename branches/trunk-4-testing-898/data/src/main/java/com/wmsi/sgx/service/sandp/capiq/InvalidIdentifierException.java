package com.wmsi.sgx.service.sandp.capiq;

import org.springframework.beans.factory.annotation.Autowired;

import com.wmsi.sgx.exception.ErrorBeanHelper;
import com.wmsi.sgx.model.ErrorBean;

public class InvalidIdentifierException extends ResponseParserException{

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private ErrorBeanHelper errorBeanHelper;


	public InvalidIdentifierException(String msg){
		super(msg);
		errorBeanHelper.addError(new ErrorBean("InvalidIdentifierException",
				msg, ErrorBean.ERROR, ""));
		errorBeanHelper.sendEmail();
	}
	
	public InvalidIdentifierException(String msg, Throwable t){
		super(msg, t);
		errorBeanHelper.addError(new ErrorBean("InvalidIdentifierException",
				msg, ErrorBean.ERROR, errorBeanHelper.getStackTrace(t)));
		errorBeanHelper.sendEmail();
	}
}
