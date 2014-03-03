package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.CompanyInfo;
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
	public CompanyInfo getCompanyInfo(String id) throws CapIQRequestException {
	
		Resource template = new ClassPathResource("META-INF/query/capiq/companyInfo.json");

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("currentDate", "02/28/2014");
		ctx.put("previousDate", "02/27/2014");
		
		String query = new CapIQRequest().parseRequest(template, ctx);

		StopWatch w = new StopWatch();
		w.start();
		
		CapIQResponse response = requestExecutor.execute(query);
		
		w.stop();
		log.error("Time taken: {} ", w.getTotalTimeMillis());
		Map<String, String> m = new HashMap<String, String>();
		
		for(CapIQResult res : response.getResults()){
			String header = res.getMnemonic();
			String val = res.getRows().get(0).getValues().get(0);
			
			/*
			for(CapIQRow r : res.getRows()){
				r.getValues().get(0);
			}
			*/
			if(m.containsKey(header)){
			
				header = header.concat("_prev");
			}
			m.put(header, val);
			//log.error(val.getRows().get(0).getValues().get(0).toString());
		}
				//m.put("IQ_MARKETCAP", "2.20");
		
		try{
			DozerBeanMapper mapper = new DozerBeanMapper();
			mapper.addMapping(new ClassPathResource("META-INF/mappings/dozer/companyInfoMapping.xml").getInputStream());
			CompanyInfo info = mapper.map(m,CompanyInfo.class);
			
			log.error("CompanyInfo {}", info);
			log.error("52WeekLow {}", info.getPriceVs52WeekLow());
			log.error("52WeekHigh {}", info.getPriceVs52WeekHigh());
			log.error("P/BV {}", info.getPriceToBookRatio());
			
			ObjectMapper jsonMapper = new ObjectMapper();
			log.error("Json {}", jsonMapper.writeValueAsString(info));

			return info;

		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;
	}
}
