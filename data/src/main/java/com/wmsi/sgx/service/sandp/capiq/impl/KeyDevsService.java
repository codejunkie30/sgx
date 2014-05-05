package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.CapIQServiceException;
import com.wmsi.sgx.service.sandp.capiq.InvalidIdentifierException;
import com.wmsi.sgx.util.DateUtil;
import com.wmsi.sgx.util.TemplateUtil;

@Service
public class KeyDevsService{

	private static final Logger log = LoggerFactory.getLogger(KeyDevsService.class);

	@Autowired
	private CapIQRequestExecutor requestExecutor;
	
	@Autowired
	KeyDevResponseParser keyDevResponseParser;

	public KeyDevs loadKeyDevelopments(String id, String asOfDate) throws CapIQRequestException, CapIQServiceException, InvalidIdentifierException {
		List<String> ids = getKeyDevelopmentIds(id, asOfDate);

		String json = getQuery(ids);
		CapIQResponse response = requestExecutor.execute(json);
		KeyDevs devs =  keyDevResponseParser.convert(response);
		devs.setTickerCode(id);
		return devs;
	}

	ClassPathResource keyDevsDataTemplate = new ClassPathResource("META-INF/query/capiq/keyDevsData.json");
	ClassPathResource requetWrapper = new ClassPathResource("META-INF/query/capiq/inputRequestsWrapper.json");

	private String getQuery(List<String> ids) throws CapIQRequestException{

		ObjectMapper m;
		JsonNode requestWrapper;
		String json = null;
		try{
			m = new ObjectMapper();
			requestWrapper = m.readTree(requetWrapper.getInputStream());
			ArrayNode requestNode = (ArrayNode) requestWrapper.get("inputRequests");

			for(String id : ids){

				Map<String, Object> ctx = new HashMap<String, Object>();
				ctx.put("id", id);

				String template = TemplateUtil.bind(keyDevsDataTemplate, ctx);
				ArrayNode arr = (ArrayNode) m.readTree(template).get("inputRequests");
				requestNode.addAll(arr);
			}
			
			json = m.writeValueAsString(requestWrapper);
		}
		catch(IOException e){
			throw new CapIQRequestException("Couldn't load key developments", e);
		}

		return json;
	}

	private CapIQRequest keyDevsIdsRequest() {
		return new CapIQRequest(new ClassPathResource("META-INF/query/capiq/keyDevs.json"));
	}

	private List<String> getKeyDevelopmentIds(String id, String asOfDate) throws CapIQRequestException {

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", DateUtil.adjustDate(asOfDate, Calendar.MONTH, -1));

		CapIQResponse response = requestExecutor.execute(keyDevsIdsRequest(), ctx);

		String err = response.getErrorMsg();
		if(StringUtils.isNotEmpty(err)){
			throw new CapIQRequestException("Error response " + err);
		}

		List<String> ids = new ArrayList<String>();
		for(CapIQResult res : response.getResults()){
			String error = res.getErrorMsg();
			if(StringUtils.isNotEmpty(error)){
				throw new CapIQRequestException("Error field " + err);
			}

			for(CapIQRow r : res.getRows()){
				String idd = r.getValues().get(0);
				if(StringUtils.isNotEmpty(idd) && !idd.equalsIgnoreCase("data unavailable")){
					ids.add(idd);
				}
			}
		}

		return ids;
	}

}
