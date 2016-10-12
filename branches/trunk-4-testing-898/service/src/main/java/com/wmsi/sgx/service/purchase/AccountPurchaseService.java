package com.wmsi.sgx.service.purchase;

/**
 * Form a message with required details for the message.
 */

public interface AccountPurchaseService {
		
	String success();
	String fail();
	String cancel();
	
	/**
   * Form a message with required details 
   * 
   * @param token String
   *            
   * @return String 
   */
	String formMessage(String token);

}
