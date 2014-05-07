package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.util.TemplateUtil;

public class CapIQRequestImpl implements CapIQRequest{
	
	private Resource template;
	
	public CapIQRequestImpl(){}
	
	public CapIQRequestImpl(Resource t){
		template = t;
	}
	
	public void setTemplate(Resource template) {
		this.template = template;
	}

	public String buildQuery(Map<String, Object> ctx) throws CapIQRequestException{
		Assert.notNull(template);
		
		try{
			return TemplateUtil.bind(template, ctx);
		}
		catch(IOException e){
			throw new CapIQRequestException("Couldn't bind parms to query template", e);
		}
	}
}
