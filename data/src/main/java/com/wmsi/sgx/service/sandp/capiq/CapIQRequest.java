package com.wmsi.sgx.service.sandp.capiq;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.wmsi.sgx.util.TemplateUtil;

public class CapIQRequest{
	
	private Resource template;
	
	public CapIQRequest(){}
	
	public CapIQRequest(Resource t){
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
