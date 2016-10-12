/**
 * 
 */
package com.wmsi.sgx.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.RSAPubkey;
import com.wmsi.sgx.service.RSAKeyService;

/**
 * This controller is used for retrieving the RSA Public Key
 * 
 * @author dt84327
 */
@RestController
@RequestMapping(produces = "application/json")
public class RSAKeyController {

	@Autowired
	private RSAKeyService rsaKeyService;

	/**
	 * Fetches RSA Public Key.
	 * 
	 * @param request
	 * @return RSAPubkey
	 * @throws Exception
	 */
	@RequestMapping(value = "publickey", method = RequestMethod.POST)
	public @ResponseBody RSAPubkey getRSAPubKey(HttpServletRequest request) throws Exception {
		RSAPubkey rsaPubkey = new RSAPubkey();
		rsaPubkey.setPubKey(rsaKeyService.getEncodedPublickey());
		rsaPubkey.setTimeStamp(Math.round(System.currentTimeMillis() / 1000.0));
		return rsaPubkey;
	}
}
