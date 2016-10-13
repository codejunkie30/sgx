package com.wmsi.sgx.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.StockListKeyDev;
import com.wmsi.sgx.model.keydevs.KeyDevsRequest;
import com.wmsi.sgx.model.keydevs.StockListKeyDevsRequest;
import com.wmsi.sgx.service.KeyDevsService;
import com.wmsi.sgx.service.ServiceException;

/**
 * 
 *This controller is used to fetch the Key Devs information which is assigned to various companies.
 */
@RestController
@RequestMapping(method=RequestMethod.POST, produces="application/json")
public class KeyDevsController{

	@Autowired
	private KeyDevsService keyDevsService;
	
	/**
	 * Retrieves key devs based on the ticker code.
	 * 
	 * @param search
	 * @return KeyDevs
	 * @throws ServiceException
	 */
	@RequestMapping("search/keydevs")
	public KeyDevs searchKeyDevs(@RequestBody KeyDevsRequest search) throws ServiceException{

		return keyDevsService.search(search);
	}
	
	/**
	 * Fetches key devs to the list of ticker code.
	 * 
	 * @param search
	 * @return List of KeyDevs
	 * @throws ServiceException
	 */
	@RequestMapping("search/stockListKeydevsTemp")
	public List<KeyDevs> searchKeyDevs(@RequestBody StockListKeyDevsRequest search) throws ServiceException {
		return keyDevsService.search(search);
	}
	
	/**
	 * Retrieves key devs on a specific search
	 * 
	 * @param search
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping("search/stockListKeydevs")
	public Map<String, List<StockListKeyDev>> searchStockListKeyDevs(@RequestBody StockListKeyDevsRequest search) throws ServiceException {
		return keyDevsService.searchKeyDevs(search);
	}

}
