package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.TemplateUtil;

@SuppressWarnings("unchecked")
public class KeyDevsService extends AbstractDataService {
	
	private ClassPathResource keyDevsDataTemplate = new ClassPathResource("META-INF/query/capiq/keyDevsData.json");
	private ClassPathResource requetWrapper = new ClassPathResource("META-INF/query/capiq/inputRequestsWrapper.json");

	@Override	
	public KeyDevs load(String id, String... parms) throws ResponseParserException, CapIQRequestException {
		String tickerNoEx = id.split(":")[0];
		Assert.notEmpty(parms);
		KeyDevs devs = getKeyDevelopments(id);
		if (devs != null) devs.setTickerCode(tickerNoEx);
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
	
	public KeyDevs getKeyDevelopments(String id)	throws ResponseParserException, CapIQRequestException {
		String tickerNoEx = id.split(":")[0];
		KeyDevs kD = new KeyDevs();
		kD.setTickerCode(tickerNoEx);
		Iterable<CSVRecord> records = getCompanyData(id, "key-devs");
		if (records == null) return null;
		List<KeyDev> list = new ArrayList<KeyDev>();
		
		List<String> idsForCapIqApiCall = new ArrayList<>();
		for (CSVRecord record : records) {
			idsForCapIqApiCall.add("IQKD"+record.get(2));
		}
		
		String json = getQuery(idsForCapIqApiCall);
		Resource template = new ByteArrayResource(json.getBytes());
		KeyDevs devs =  executeRequest(new CapIQRequestImpl(template), null);
		
		Map<String, String> keyDevSource = new HashMap<String, String>();
		for(KeyDev dev : devs.getKeyDevs()){
			keyDevSource.put(dev.getId(),dev.getSource());
		}
		
		for (CSVRecord record : records) {
			KeyDev keydev = new KeyDev();
			keydev.setSource(keyDevSource.get("IQKD"+record.get(2)));
			keydev.setDate(new Date(record.get(3)));
			keydev.setHeadline(record.get(4));
			keydev.setSituation(record.get(5));
			keydev.setType(record.get(6));
			list.add(keydev);
		}
		kD.setKeyDevs(list);
		return kD;
	}

}
