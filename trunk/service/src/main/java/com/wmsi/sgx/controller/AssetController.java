package com.wmsi.sgx.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.Asset;
import com.wmsi.sgx.domain.Price;
import com.wmsi.sgx.service.AssetService;

@RestController
@RequestMapping("/assets")
public class AssetController{

	private static final Logger log = LoggerFactory.getLogger(AssetController.class);
	
	@Autowired
	private AssetService assetService;
	
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public Asset save(@RequestBody Asset c) {
		return assetService.save(c);
	}

	@RequestMapping(value = "saveAll", method = RequestMethod.POST)
	public List<Asset> save(@RequestBody List<Asset> c) {
		return assetService.save(c);
	}

	@RequestMapping(value = "savePrice", method = RequestMethod.POST)
	public Price save(@RequestBody Price p) {
		
		Asset a = assetService.findByTicker("C6L");
		p.setAsset(a);
		return assetService.save(p);
	}

	@RequestMapping(value = "get", method = RequestMethod.GET)
	public Asset get(@RequestParam("ticker") String ticker) {
		return assetService.findByTicker(ticker);
	}

}
