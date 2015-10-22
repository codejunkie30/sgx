package com.wmsi.sgx.service.indexer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.integration.annotation.Header;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wmsi.sgx.model.indexer.Indexes;
import com.wmsi.sgx.service.indexer.IndexerService;
import com.wmsi.sgx.service.indexer.IndexerServiceException;

public class IndexerServiceImpl implements IndexerService{

	private Logger log = LoggerFactory.getLogger(IndexerServiceImpl.class);
	
	@Value("${elasticsearch.url}")
	private String esUrl;
	
	@Value("${elasticsearch.index.prefix}")
	private String indexPrefix;
	
	@Value("${elasticsearch.index.name}")
	private String indexAlias;
	
	private Resource indexMappingResource = new ClassPathResource("META-INF/mappings/elasticsearch/sgx-mapping.json");
	
	private RestTemplate restTemplate;
	public void setRestTemplate(RestTemplate t){
		restTemplate = t;
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
	}
	
	@Override
	@Transactional
	public synchronized Boolean createIndex(@Header String indexName) throws IOException, IndexerServiceException{
		URI indexUri = buildUri("/" + indexName);
		
		ClientHttpResponse headResponse = restTemplate.getRequestFactory().createRequest(indexUri, HttpMethod.HEAD).execute();
		boolean indexExists = headResponse.getStatusCode() == HttpStatus.OK;
		
		if(indexExists){
			log.debug("Index exists " + indexExists);
			return true;
		}
			
		ObjectMapper mapper = new ObjectMapper();
		JsonNode readTree = mapper.readTree(indexMappingResource.getInputStream());

		int statusCode = postJson(indexUri, readTree);
		
		// Check for 200 range response code
		if(statusCode / 100 != 2){			
			throw new IndexerServiceException("Create index request returned " + statusCode + " http response code. Could not create index");
		}		
		
		return true;
	}
	
	@Override
	@Transactional		
	public synchronized Boolean createIndexAlias(@Header String indexName) throws IndexerServiceException {
		URI indexUri = buildUri("/_aliases");
		JsonNode alias = buildAliasJson(indexName);
		
		log.debug("Updating index aliases {}", alias);
			
		int statusCode = postJson(indexUri, alias);
			
		// Check for 200 range response code
		if(statusCode / 100 != 2){	
			throw new IndexerServiceException("Create alias request returned " + statusCode + " http response code. Could not create alias.");
		}		
		
		return true;
	}

	@Override
	public Indexes getIndexes() throws IndexerServiceException {
		URI indexUri = buildUri("/_aliases");		
		return restTemplate.getForObject(indexUri, Indexes.class);
	}
	
	@Override
	@Transactional
	public Boolean save(String type, String id, Object obj, String indexName) throws IndexerServiceException {
		
        UriComponents uriComp = UriComponentsBuilder.fromUriString(esUrl + "/{indexName}/{type}/{id}?opt_type=create").build();
        URI uri = uriComp.expand(indexName, type, id).toUri();
        
        ResponseEntity<Object> res = restTemplate.exchange(uri, HttpMethod.PUT, buildEntity(obj), Object.class);
        
        int statusCode = res.getStatusCode().value();
        
		// Check for 200 range response code
		if(statusCode / 100 != 2) throw new IndexerServiceException("Error indexing object: " + statusCode + " http response code.");

        return true;
	}

	@Override
	@Transactional
	public Boolean bulkSave(String type, String body, String indexName) throws IndexerServiceException {
		
        UriComponents uriComp = UriComponentsBuilder.fromUriString(esUrl + "/{indexName}/{type}/_bulk").build();
        URI uri = uriComp.expand(indexName, type).toUri();
        
        ResponseEntity<Object> res = restTemplate.exchange(uri, HttpMethod.PUT, buildEntityText(body), Object.class);
        
        int statusCode = res.getStatusCode().value();
        
		// Check for 200 range response code
		if(statusCode / 100 != 2) throw new IndexerServiceException("Error indexing object: " + statusCode + " http response code.");

        return true;
	}
	
	@Override
	@Transactional
	public Boolean deleteIndex(String indexName) throws IndexerServiceException {
		
        UriComponents uriComp = UriComponentsBuilder.fromUriString(esUrl + "/{indexName}").build();
        URI uri = uriComp.expand(indexName).toUri();

        log.debug("Deleting index {}", indexName);
        
        restTemplate.delete(uri);
        
        log.debug("Deleted index {} successfully", indexName);
        
        return true;
	}

	private URI buildUri(String path) throws IndexerServiceException{
		try{
			return new URI(esUrl + path);
		}
		catch(URISyntaxException e){
			throw new IndexerServiceException("Invalid uri syntax for alaias request.", e);
		}
	}
	
	private JsonNode buildAliasJson(String indexName){
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode removeAlias = mapper.createObjectNode();
		ObjectNode remove = mapper.createObjectNode();
		
		removeAlias.put("index", indexPrefix.concat("*"));
		removeAlias.put("alias", indexAlias);
		remove.put("remove",  removeAlias);
		
		ObjectNode addAlias = mapper.createObjectNode();
		ObjectNode add = mapper.createObjectNode(); 

		addAlias.put("index", indexName);
		addAlias.put("alias", indexAlias);
		add.put("add", addAlias);
		
		ArrayNode actions = mapper.createArrayNode();
		actions.add(remove);
		actions.add(add);
		
		ObjectNode alias = mapper.createObjectNode();
		alias.put("actions",  actions);

		return alias;
	}

	private <T> HttpEntity<String> buildEntityText(String txt){
		HttpHeaders headers = new HttpHeaders();
		Charset utf8 = Charset.forName("UTF-8");
		headers.setContentType(MediaType.TEXT_PLAIN);
		headers.setAcceptCharset(Collections.singletonList(utf8));
		return new HttpEntity<String>(txt, headers);		
	}
	
	private int postJson(URI uri, Object json){
		HttpEntity<?> entity = buildEntity(json);
		ResponseEntity<Object> res = restTemplate.exchange(uri, HttpMethod.POST, entity, Object.class);		
		return res.getStatusCode().value();		
	}
	
	private <T> HttpEntity<T> buildEntity(T json){
		HttpHeaders headers = new HttpHeaders();
		Charset utf8 = Charset.forName("UTF-8");
		MediaType mediaType = new MediaType("application", "json", utf8);
		headers.setContentType(mediaType);
		headers.setAcceptCharset(Collections.singletonList(utf8));
		return new HttpEntity<T>(json, headers);		
	}

}
