package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQResponseConversionException;
import com.wmsi.sgx.service.sandp.capiq.RequestExecutor;

/**
 * Class for handling requests to the Capital IQ API Rest Service
 * @author Justin Lee
 */
public class CapIQRequestExecutor implements RequestExecutor{

	private Logger log = LoggerFactory.getLogger(CapIQRequestExecutor.class);
	private ObjectMapper mapper = new ObjectMapper();

	private RestTemplate restTemplate;
	public void setRestTemplate(RestTemplate t){restTemplate = t;}

	private String url;
	public void setUrl(String u){url = u;}
	
	private String userId;
	public void setUserId(String u){userId = u;}
	
	private static final String PARAM_INPUTS = "inputRequests";
	private static final String PARAM_USERID = "userId";
	
	/**
	 * Execute CapIQRequest to get the CapIQResponse from CapIQ Rest services
	 * @param CapIQRequestImpl
	 * @param context
	 * @return CapIQResponse 
	 * @throws CapIQRequestException
	 */
	@Override
	public CapIQResponse execute(CapIQRequestImpl req, Map<String, Object> ctx) throws CapIQRequestException {

		log.debug("Executing request to capital iq api");

		ResponseEntity<CapIQResponse> res = null;

		String query = req.buildQuery(ctx);
		HttpEntity<MultiValueMap<String, String>> entity = buildEntity(query);

		try{
			
			res = restTemplate.exchange(url, HttpMethod.POST, entity, CapIQResponse.class);
			
			if(res == null)
				throw new CapIQRequestException("Null response received from capitalIQ api");
		}
		catch(HttpMessageNotReadableException ex){
			// Catch any errors transforming the api response. The CapIQ api can be
			// very inconsistent with it's json structure. Occasionally fields that are strings
			// on good response are arrays on errors. Since we have no way of knowing which fields
			// are affected, we'll simply throw an exception as something has already gone wrong.
			throw new CapIQResponseConversionException("Could not convert response from api", ex);
		}
		catch(HttpClientErrorException | HttpServerErrorException ex){
			
			CapIQRequestException exception = new CapIQRequestException("Recieved error code response from api server", ex);			
			
			exception.setStatusCode(ex.getStatusCode().toString());
			exception.setStatusText(ex.getStatusText());
			
			exception.setRequestHeaders(entity.getHeaders().toString());
			exception.setRequestBody(entity.getBody().toString());
			
			exception.setResponseHeaders(ex.getResponseHeaders().toString());
			exception.setResponseBody(ex.getResponseBodyAsString());			
			
			throw exception;
		}
		
		int statusCode = res.getStatusCode().value();
		
		if(statusCode / 100 != 2){
			log.debug("Request returned error status code {}", statusCode);
			throw new CapIQRequestException("Api returned an error status");
		}
		
		log.debug("Capital IQ api returned successful response code: {}", statusCode);
		
		return res.getBody();
	}
	
	private HttpHeaders buildHeaders(){
		
		MediaType mediaType = new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8"));		
		
		HttpHeaders headers = new HttpHeaders();		
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
	
	/**
	 * Load files
	 * @param path
	 * @return List of file names
	 */
	public List<String> loadFiles(String beginsWith, String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		List<String> matches = new ArrayList<String>();
		for(File file: listOfFiles){
			if (file.getName().startsWith(beginsWith)){
				matches.add(file.getName());
			}		
		}
		Collections.sort(matches);
		return matches;
	}
	
	/**
	 * Read CapIQResponse
	 * @param path
	 * @return CapIQResponse
	 */
	public CapIQResponse readFile(String path){
		try {
			return mapper.readValue(new File(path), CapIQResponse.class);
	
		} catch (JsonParseException e1) {
			log.error("Failed to read " + e1);
		} catch (JsonMappingException e1) {
			log.error("Failed to map file " + e1);
		} catch (IOException e1) {
			log.error("Failed IO " + e1);
		}
		return null;
	}
	
	/**
	 * Write CapIQResponse into files
	 * @param CapIQResponse
	 * @param path
	 * @return
	 */
	public void writeFile(String path2, CapIQResponse response){
		try {
			mapper.writeValue(new File(path2), response);
		} catch (JsonGenerationException e) {
			log.error("Failed to write: " + e);
		} catch (JsonMappingException e) {
			log.error("Failed to map file: " + e);
		} catch (IOException e) {
			log.error("Failed IO " + e);
		}
	}
}
