package com.wmsi.sgx.service.purchase;

/**
 * The AccountPurchaseService handles the information for the Account purchase
 * operation
 */
public interface AccountPurchaseService {
		
	String success();
	String fail();
	String cancel();
	
	/**
	 * Returns the message with the required details to make a purchase
	 * 
	 * @param token
	 * @return
	 */
	String formMessage(String token);

}
