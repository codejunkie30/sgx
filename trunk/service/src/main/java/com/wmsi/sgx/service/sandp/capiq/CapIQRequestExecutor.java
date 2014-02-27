package com.wmsi.sgx.service.sandp.capiq;

import java.nio.charset.Charset;

import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;

public class CapIQRequestExecutor{

	private Logger log = LoggerFactory.getLogger(CapIQRequestExecutor.class);

	private RestTemplate restTemplate;
	public void setRestTemplate(RestTemplate t){restTemplate = t;}

	private String url;
	public void setUrl(String u){url = u;}
	
	private String userId;
	public void setUserId(String u){userId = u;}
	
	private static final String PARAM_INPUTS = "inputRequests";
	private static final String PARAM_USERID = "userId";
	
	public CapIQResponse execute(String query) throws CapIQRequestException {
		
		log.debug("Excuting request to capital iq api");
		ResponseEntity<CapIQResponse> res = null;
		
		try{
			res = restTemplate.exchange(url, HttpMethod.POST, buildEntity(query), CapIQResponse.class);
			
			if(res == null)
				throw new CapIQRequestException("Null response received from capitalIQ api");
		}
		catch(HttpMessageNotReadableException ex){
			// Catch any errors transforming the api response. The CapIQ api can be
			// very inconsistent with it's json structure. Occasionally fields that are strings
			// on good response are arrays on errors. Since we can't know how 
			throw new CapIQRequestException("Could not convert response from api", ex);
		}
		
		int statusCode = res.getStatusCode().value();
		
		if(statusCode / 100 != 2){
			log.debug("Request returned error status code {}", statusCode);
			throw new CapIQRequestException("Api returned an error status");
		}
		
		return res.getBody();
	}
	
	private HttpHeaders buildHeaders(){
		HttpHeaders headers = new HttpHeaders();		
		MediaType mediaType = new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8"));		
		headers.setContentType(mediaType);
		headers.setConnection(HTTP.CONN_KEEP_ALIVE);
		return headers;
	}
	
	private HttpEntity<MultiValueMap<String, String>>  buildEntity(String query){
		
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add(PARAM_INPUTS, query);
		body.add(PARAM_USERID, userId);

		return new HttpEntity<MultiValueMap<String, String>>(body, buildHeaders());
	}
}
