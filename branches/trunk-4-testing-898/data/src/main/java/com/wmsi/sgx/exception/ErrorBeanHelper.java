package com.wmsi.sgx.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.model.ErrorBean;

public class ErrorBeanHelper {

	public static final String DATALOAD_COMPLETE_WITH_ERRORS = " Dataload failed with errors  ";
	
	public static final String DATALOAD_COMPLETE_SUCCESSFULLY = " Dataload complete successfully  ";

	@Value("${email.dataload.complete}")
	public String toSite;

	@Autowired
	private com.wmsi.sgx.util.EmailService emailService;

	Map<String, ArrayList<ErrorBean>> errorMap = new HashMap<String, ArrayList<ErrorBean>>();
	
	private Logger log = LoggerFactory.getLogger(ErrorBeanHelper.class);

	/**
	 * Contains any errors that may have occurred while executing the request.
	 */
	private ArrayList<ErrorBean> errors;

	/**
	 * @return the errors
	 */
	public List<ErrorBean> getErrors() {
		if (this.errors == null) {
			this.errors = new ArrayList<ErrorBean>();
		}
		return this.errors;
	}

	/**
	 * Adds a ErrorBean to the errors list
	 * 
	 * @param error
	 *            the ErrorBean to add
	 */
	public void addError(ErrorBean error) {
		if (this.errors == null) {
			this.errors = new ArrayList<ErrorBean>();
		}
		if (errorMap.get(error.getErrorCode()) != null) {
			errorMap.get(error.getErrorCode()).add(error);
		} else {
			this.errors.add(error);
			errorMap.put(error.getErrorCode(), (ArrayList<ErrorBean>) errors);
		}

	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<Entry<String, ArrayList<ErrorBean>>>  it = errorMap.entrySet().iterator();
		while (it.hasNext()) {
			StringBuffer beanInfo = new StringBuffer();
			Map.Entry pair = (Map.Entry) it.next();
			ArrayList<ErrorBean> beanList = (ArrayList<ErrorBean>) pair.getValue();
			for (ErrorBean b : beanList) {
				beanInfo.append(" \n Error Message :- \n" + b.getErrorMessage());
				beanInfo.append(" \n ");
				beanInfo.append("  Object Description :- \n" + truncateString(b.getObjectName()));
			}
			sb.append(pair.getKey() + " : " + beanInfo.toString() + "\n");
			log.info(" ***** Error Information ******* :- \n "+ sb.toString());
		}
		return sb.toString();

	}
	
	private String truncateString(String str){
		if(StringUtils.isEmpty(str))return "";
		String lines[] = str.split("\\r?\\n");
		StringBuffer sb = new StringBuffer();
		int i = 1;
		for(String s:lines){
			sb.append(s);
			i++;
			if(i==7){
				return sb.toString();
			}
		}
		return sb.toString().isEmpty()?str.substring(0, str.length()/2):sb.toString();
	}

	public void sendEmail() {
		try {
			if(errorMap.isEmpty()){
				emailService.send(toSite, DATALOAD_COMPLETE_SUCCESSFULLY, "");
			}else{
				emailService.send(toSite, DATALOAD_COMPLETE_WITH_ERRORS, this.toString());
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getStackTrace(Exception ex){
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

	public String getStackTrace(Throwable t){
		StringWriter errors = new StringWriter();
		t.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

	public void sendEmail(Throwable t){
		try{
			emailService.send(toSite, DATALOAD_COMPLETE_WITH_ERRORS, getStackTrace(t));
		}catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
