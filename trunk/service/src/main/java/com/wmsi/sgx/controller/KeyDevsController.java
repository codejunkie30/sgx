package com.wmsi.sgx.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.keydevs.KeyDevsRequest;
import com.wmsi.sgx.service.KeyDevsService;
import com.wmsi.sgx.service.ServiceException;

@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class KeyDevsController{

	@Autowired
	private KeyDevsService keyDevsService;
	
	@RequestMapping("search/keydevs")
	public KeyDevs searchKeyDevs(@RequestBody KeyDevsRequest search) throws ServiceException{

		return keyDevsService.search(search);
	}
	
}
