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
 * @author dt84327
 */
@RestController
@RequestMapping(produces = "application/json")
public class RSAKeyController {

	@Autowired
	private RSAKeyService rsaKeyService;

	@RequestMapping(value = "publickey", method = RequestMethod.POST)
	public @ResponseBody RSAPubkey getRSAPubKey(HttpServletRequest request) throws Exception {
		RSAPubkey rsaPubkey = new RSAPubkey();
		rsaPubkey.setPubKey(rsaKeyService.getEncodedPublickey());
		return rsaPubkey;
	}
}
