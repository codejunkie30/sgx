package com.wmsi.sgx.security;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Class for generating random secure tokens for use as indentifiers in 
 * session, password reset requests, email confirmations etc. 
 * 
 * @author JLee
 */
public class SecureTokenGenerator{

	private static final int BITS = 90;
	private static final int BASE = 32;

	private SecureRandom random = new SecureRandom();
	
	public String nextToken() {
		return new BigInteger(BITS, random).toString(BASE);
	}
}
