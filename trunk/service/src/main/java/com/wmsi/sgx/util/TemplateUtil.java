package com.wmsi.sgx.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;

public class TemplateUtil{

	public static String bind(Resource res, Map<String, Object> ctx) throws IOException{
		String template = FileUtils.readFileToString(res.getFile());		
		return bind(template, ctx);
	}

	public static String bind(String template, Map<String, Object> ctx){
		StringTemplate st = new StringTemplate(template);
		Iterator<Entry<String, Object>> i = ctx.entrySet().iterator();
		
		while(i.hasNext()){
			Entry<String, Object> entry = i.next();
			st.setAttribute(entry.getKey(), entry.getValue());
		}
		
		return st.toString();
	}
}
