package com.wmsi.sgx.service.indexer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


public class IndexerService{

	private Logger log = LoggerFactory.getLogger(IndexerService.class);
	
	private String esUrl = "http://localhost:9200";
	private String indexNamePrefix = "sgx_";
	
	private Resource indexMappingResource = new ClassPathResource("META-INF/mappings/elasticsearch/sgx-mapping.json");
	
	@Autowired	
	private RestTemplate restTemplate;
	private void setRestTemplate(RestTemplate t){restTemplate = t;}
	
	public synchronized void createIndex(@Header String asOfDate) throws IOException, URISyntaxException, IndexerServiceException{
		
		String indexName = indexNamePrefix + asOfDate;
		URI indexUri = new URI(esUrl + "/" + indexName);
		
		ClientHttpResponse headResponse = restTemplate.getRequestFactory().createRequest(indexUri, HttpMethod.HEAD).execute();
		boolean indexExists = headResponse.getStatusCode() == HttpStatus.OK;
		
		if(indexExists){
			log.debug("Index exists " + indexExists);
			return;
		}
			
		File f = indexMappingResource.getFile();
		String indexMapping = FileUtils.readFileToString(f);

		int statusCode = postJson(indexUri, indexMapping);
		
		// Check for 200 range response code
		if(statusCode / 100 != 2){			
			throw new IndexerServiceException("Create index request returned " + statusCode + " http response code. Could not create index");
		}		
	}

	@Transactional
	public void save(@Header String persistId, @Header String type, @Header String asOfDate, @Payload String json) throws IOException{
        String indexName = indexNamePrefix + asOfDate;
        HttpEntity<String> entity = buildEntity(json);
	    ResponseEntity<String> res = restTemplate.exchange(esUrl + "/"+ indexName + "/" + type + "/" + persistId + "?opt_type=create", HttpMethod.PUT, entity, String.class);        
	}

	private HttpEntity<String> buildEntity(String json){
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
		headers.setContentType(mediaType);
		headers.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
	
		return new HttpEntity<String>(json, headers);		
	}
	
	private int postJson(URI uri, String json){
		HttpEntity<String> entity = buildEntity(json);
		ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);		
		return res.getStatusCode().value();		
	}
}
