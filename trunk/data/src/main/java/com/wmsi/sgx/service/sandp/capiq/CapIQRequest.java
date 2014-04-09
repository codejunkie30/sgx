package com.wmsi.sgx.service.sandp.capiq;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;

public class CapIQRequest{

	//private Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	// TODO Refactor into class path string from file yparser
	public String parseRequest(Resource template, Map<String, Object> ctx) throws CapIQRequestException{
		String query = null;
		
		try{
			String queryTemplate = FileUtils.readFileToString(template.getFile());
			StringTemplate st = new StringTemplate(queryTemplate);
			
			Iterator<Entry<String, Object>> i = ctx.entrySet().iterator();
			
			while(i.hasNext()){
				Entry<String, Object> entry = i.next();
				st.setAttribute(entry.getKey(), entry.getValue());
			}
			
			query = st.toString();
		}
		catch(IOException e){
			throw new CapIQRequestException("IOError building input request", e);
		}
		
		return query;
	}
}
