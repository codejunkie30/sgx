/**
 * 
 */
package com.wmsi.sgx.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.security.RSAKeyUtility;
import com.wmsi.sgx.service.RSAKeyException;
import com.wmsi.sgx.service.RSAKeyService;

/**
 * @author dt84327
 *
 */
@Service
public class RSAKeyServiceImpl implements RSAKeyService {

	/**
	 * Path to store the public key
	 */
	@Value("${rsakey.filepath.public}")
	private String publicPath;

	/**
	 * Path to store private key
	 */
	@Value("${rsakey.filepath.private}")
	private String privatePath;

	@Override
	public String getEncodedPublickey() throws RSAKeyException {
		return RSAKeyUtility.getEncodedPubkey(publicPath);
	}

	@Override
	public String decrypt(String toBeDecrypted) throws RSAKeyException {
		return RSAKeyUtility.decrypt(toBeDecrypted, privatePath);
	}
	
	public byte[] encrypt(String toBeEncrypted) throws RSAKeyException{
		return RSAKeyUtility.encrypt(toBeEncrypted,publicPath);
	}
}
