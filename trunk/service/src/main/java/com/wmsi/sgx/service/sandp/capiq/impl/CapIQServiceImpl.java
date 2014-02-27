package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;

@Service
public class CapIQServiceImpl implements CapIQService{

	private Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	@Autowired
	private CapIQRequestExecutor requestExecutor;
	
	// TODO Refactor - proof of concept
	@Override
	public void getCompanyInfo() throws CapIQRequestException {
		/*
		String query = null;
		
		Resource resource = new ClassPathResource("META-INF/query/capiq/companyInfo.json");
		String queryTemplate;
		try{
			queryTemplate = FileUtils.readFileToString(resource.getFile());
			query = queryTemplate.replaceAll("\\$id", "C6L");
		}
		catch(IOException e1){
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		String query = new CapIQRequest().getRequest("C6L", new Date());
		

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
