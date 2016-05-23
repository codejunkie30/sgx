/**
 * 
 */
package com.wmsi.sgx.service;

/**
 * @author dt84327
 *
 */
public interface RSAKeyService {

	String getEncodedPublickey() throws RSAKeyException;
	String decrypt(String toBeDecrypted) throws RSAKeyException;
}
