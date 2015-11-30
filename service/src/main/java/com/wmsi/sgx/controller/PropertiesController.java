package com.wmsi.sgx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.model.account.TrialResponse;

@RestController
@RequestMapping(produces="application/json")
public class PropertiesController {
	private static final Logger log = LoggerFactory.getLogger(PropertiesController.class);
	
	@Value("${halfway.trial.duration}")
	public String halfwayDuration;
	@Value("${full.trial.duration}")
	public String trialDuration;
	
	@RequestMapping(value = "properties/trialDuration", method=RequestMethod.POST)
	public TrialResponse getTrialDays(){
		TrialResponse ret = new TrialResponse();
		ret.setHalfwayDays(halfwayDuration);
		ret.setTrialDays(trialDuration);
		return ret;
		
	}
}
