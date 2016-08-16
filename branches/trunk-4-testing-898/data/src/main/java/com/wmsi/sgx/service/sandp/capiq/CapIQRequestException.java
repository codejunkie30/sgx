package com.wmsi.sgx.service.sandp.capiq;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;
import com.wmsi.sgx.exception.ErrorBeanHelper;
import com.wmsi.sgx.model.ErrorBean;

public class CapIQRequestException extends Exception{

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private ErrorBeanHelper errorBeanHelper;


	public CapIQRequestException(String msg){
		super(msg);
		errorBeanHelper.addError(new ErrorBean("AlphaFactorServiceException",
				msg, ErrorBean.ERROR, ""));
		errorBeanHelper.sendEmail();

	}

	public CapIQRequestException(String msg, Throwable e){
		super(msg, e);
		errorBeanHelper.addError(new ErrorBean("AlphaFactorServiceException",
				msg, ErrorBean.ERROR, errorBeanHelper.getStackTrace(e)));
		errorBeanHelper.sendEmail();

	}

	private String statusCode;
	private String statusText;
	private String requestHeaders;
	private String responseHeaders;
	private String requestBody;
	private String responseBody;

	public String getStatusCode() {
		return statusCode;
	}

	public String getStatusText() {
		return statusText;
	}

	public String getRequestHeaders() {
		return requestHeaders;
	}

	public String getResponseHeaders() {
		return responseHeaders;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public void setRequestHeaders(String requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public void setResponseHeaders(String responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(super.hashCode(), statusCode, statusText, requestHeaders, responseHeaders, requestBody, responseBody);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof CapIQRequestException) {
			if (!super.equals(object)) 
				return false;
			CapIQRequestException that = (CapIQRequestException) object;
			return Objects.equal(this.statusCode, that.statusCode)
				&& Objects.equal(this.statusText, that.statusText)
				&& Objects.equal(this.requestHeaders, that.requestHeaders)
				&& Objects.equal(this.responseHeaders, that.responseHeaders)
				&& Objects.equal(this.requestBody, that.requestBody)
				&& Objects.equal(this.responseBody, that.responseBody);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("super", super.toString())
			.add("serialVersionUID", serialVersionUID)
			.add("statusCode", statusCode)
			.add("statusText", statusText)
			.add("requestHeaders", requestHeaders)
			.add("responseHeaders", responseHeaders)
			.add("requestBody", requestBody)
			.add("responseBody", responseBody)
			.toString();
	}
}
