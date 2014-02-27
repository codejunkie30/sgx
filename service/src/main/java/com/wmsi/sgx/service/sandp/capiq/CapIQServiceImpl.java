package com.wmsi.sgx.service.sandp.capiq;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;

@Service
public class CapIQServiceImpl{

	private Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	@Autowired
	private CapIQRequestExecutor requestExecutor;
	
	// TODO Refactor - proof of concept
	public void getCompanyInfo() throws IOException {
		String query = null;
		Resource resource = new ClassPathResource(
				"META-INF/query/capiq/companyInfo.json");
		String queryTemplate = FileUtils.readFileToString(resource.getFile());
		query = queryTemplate.replaceAll("\\$id", "C6L");

		StopWatch w = new StopWatch();
		w.start();
		
		CapIQResponse response = null;
		
		try{
			response = requestExecutor.execute(query);
		}
		catch(CapIQRequestException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		w.stop();
		log.error("Time taken: {} ", w.getTotalTimeMillis());
		
		for(CapIQResult val : response.getResults()){
			log.error(val.getRows().get(0).getValues().get(0).toString());
		}
		
	}
}
