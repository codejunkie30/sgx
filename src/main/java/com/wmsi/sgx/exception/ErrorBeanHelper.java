package com.wmsi.sgx.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.model.ErrorBean;

public class ErrorBeanHelper {

	public static final String DATALOAD_COMPLETE_WITH_ERRORS = " Dataload failed with errors  ";

	@Value("${email.dataload.complete}")
	public String toSite;

	@Autowired
	private com.wmsi.sgx.util.EmailService emailService;

	Map<String, ArrayList<ErrorBean>> errorMap = new HashMap<String, ArrayList<ErrorBean>>();

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
			errorMap.get(error).add(error);
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
				beanInfo.append(" ************* Error Message ********* \n " + b.getErrorMessage());
				beanInfo.append(" \n ");
				beanInfo.append(" ************* Object Description ********* \n " + b.getObjectName());
			}
			sb.append(pair.getKey() + " : " + beanInfo.toString() + "\n");
		}
		return sb.toString();

	}

	public void sendEmail() {
		try {
			emailService.send(toSite, DATALOAD_COMPLETE_WITH_ERRORS, this.toString());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
