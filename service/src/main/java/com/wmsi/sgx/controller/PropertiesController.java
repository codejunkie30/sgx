package com.wmsi.sgx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.config.AppConfig.TrialProperty;
import com.wmsi.sgx.model.account.TrialResponse;

/**
 * This controller is used to read the properties from properties file.
 *
 */
@RestController
@RequestMapping(produces="application/json")
public class PropertiesController {
	private static final Logger log = LoggerFactory.getLogger(PropertiesController.class);
	
	@Autowired
	private TrialProperty getTrial;
	
	/**
	 * Returns the trial days.
	 * 
	 * @return TrialResponse
	 */
	@RequestMapping(value = "properties/trialDuration", method=RequestMethod.POST)
	public TrialResponse getTrialDays(){
		TrialResponse ret = new TrialResponse();
		ret.setHalfwayDays(getTrial.getHalfway());
		ret.setTrialDays(getTrial.getTrial());
		return ret;
		
	}
}
