package com.wmsi.sgx.service.sandp.capiq;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class CapIQRequest{

	//private Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	private Resource template = new ClassPathResource("META-INF/query/capiq/companyInfo.json");
	
	public String getRequest(String id, Date date) throws CapIQRequestException{
		String query = null;
		
		try{
			String queryTemplate = FileUtils.readFileToString(template.getFile());
			query = queryTemplate.replaceAll("\\$id", id);
		}
		catch(IOException e){
			throw new CapIQRequestException("IOError building input request", e);
		}
		
		return query;
	}
}
