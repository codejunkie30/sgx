package com.wmsi.sgx.service.purchase.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.wiz.enets2.transaction.umapi.CreditMerchant;
import com.wiz.enets2.transaction.umapi.Merchant;
import com.wiz.enets2.transaction.umapi.data.CreditTxnReq;
import com.wiz.enets2.transaction.umapi.data.TxnReq;
import com.wmsi.sgx.security.SecureTokenGenerator;
import com.wmsi.sgx.service.purchase.AccountPurchaseService;

/**
 * The AccountPurchaseServiceImpl class handles the information for the Account purchase
 * operation
 */
public class AccountPurchaseServiceImpl implements AccountPurchaseService{

	@Override
	public String success() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String fail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cancel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Returns the message with the required details to make a purchase
	 * 
	 * @param token
	 * @return String message as a string
	 */
	@Override
	public String formMessage(String token) {
		TxnReq req = new TxnReq();
		CreditTxnReq credReq = new CreditTxnReq();
		req.setNetsMid("897964002");
		req.setPaymentMode("CC");
		req.setTxnAmount("1000");
		req.setCurrencyCode("SGD");
		req.setMerchantTxnRef(token);
		req.setSubmissionMode("B");
		req.setMerchantCertId("1");
		req.setSuccessUrl("http://localhost:8080/mvc/success");
		req.setSuccessUrlParams("");
		req.setFailureUrl("http://localhost:8080/mvc/fail");
		req.setFailureUrlParams("");
		
		credReq.setPaymentType("SALE");
		credReq.setCancelUrl("http://localhost:8080/mvc/cancel");

		req.setCreditTxnReq(credReq);		
		
		CreditMerchant m = (CreditMerchant) Merchant.getInstance (Merchant.MERCHANT_TYPE_CREDIT);
		
		String sMsg = m.formPayReq(req);
		
		return sMsg;
	}
}
