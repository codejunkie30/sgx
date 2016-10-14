package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.util.TemplateUtil;
import com.google.common.base.Objects;

public class CapIQRequestImpl implements CapIQRequest{
	
	private Resource template;
	
	public CapIQRequestImpl(){}
	
	public CapIQRequestImpl(Resource t){
		template = t;
	}
	
	public void setTemplate(Resource template) {
		this.template = template;
	}
	
	/**
	 * Build CapIQQuery 
	 * @param context
	 * @return query
	 * @throws CapIQRequestException
	 */
	@Override
	public String buildQuery(Map<String, Object> ctx) throws CapIQRequestException{
		Assert.notNull(template);
		
		try{
			return TemplateUtil.bind(template, ctx);
		}
		catch(IOException e){
			throw new CapIQRequestException("Couldn't bind parms to query template", e);
		}
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(super.hashCode(), template);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof CapIQRequestImpl) {
			if (!super.equals(object)) 
				return false;
			CapIQRequestImpl that = (CapIQRequestImpl) object;
			return Objects.equal(this.template, that.template);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("super", super.toString())
			.add("template", template)
			.toString();
	}
	
}
