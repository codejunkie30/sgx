package com.wmsi.sgx.service.sandp.capiq;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

public class CapIQRequest{

	// TODO Refactor into class path string from file yparser
	public String parseRequest(Resource template, Map<String, Object> ctx) throws CapIQRequestException{
		String queryTemplate = getTemplateString(template);
		StringTemplate st = new StringTemplate(queryTemplate);
		
		Iterator<Entry<String, Object>> i = ctx.entrySet().iterator();
		
		while(i.hasNext()){
			Entry<String, Object> entry = i.next();
			st.setAttribute(entry.getKey(), entry.getValue());
		}
		
		return st.toString();		
	}
	
	private String getTemplateString(Resource template) throws CapIQRequestException{		
		try{
			StringWriter writer = new StringWriter();
			IOUtils.copy(template.getInputStream(), writer, "UTF-8");
			return writer.toString();
		}
		catch(IOException e){
			throw new CapIQRequestException("Could not load request tempalte", e);
		}

	}
}
