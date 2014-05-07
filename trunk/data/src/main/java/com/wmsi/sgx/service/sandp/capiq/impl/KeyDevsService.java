package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;
import com.wmsi.sgx.util.TemplateUtil;

@SuppressWarnings("unchecked")
public class KeyDevsService extends AbstractDataService {

	private ClassPathResource keyDevsIdsTemplate = new ClassPathResource("META-INF/query/capiq/keyDevs.json");
	private ClassPathResource keyDevsDataTemplate = new ClassPathResource("META-INF/query/capiq/keyDevsData.json");
	private ClassPathResource requetWrapper = new ClassPathResource("META-INF/query/capiq/inputRequestsWrapper.json");

	@Override	
	public KeyDevs load(String id, String... parms) throws ResponseParserException, CapIQRequestException {
		
		Assert.notEmpty(parms);
		
		List<String> ids = getKeyDevelopmentIds(id, parms[0]);

		String json = getQuery(ids);
		
		Resource template = new ByteArrayResource(json.getBytes());
		KeyDevs devs =  executeRequest(new CapIQRequestImpl(template), null);
		
		devs.setTickerCode(id);
		return devs;
	}

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

	private List<String> getKeyDevelopmentIds(String id, String asOfDate) throws CapIQRequestException {

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", DateUtil.adjustDate(asOfDate, Calendar.MONTH, -1));

		CapIQResponse response = requestExecutor.execute(new CapIQRequestImpl(keyDevsIdsTemplate), ctx);

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
