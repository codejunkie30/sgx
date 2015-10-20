package com.wmsi.sgx.service.purchase;

public interface AccountPurchaseService {
		
	String success();
	String fail();
	String cancel();
	String formMessage(String token);

}
