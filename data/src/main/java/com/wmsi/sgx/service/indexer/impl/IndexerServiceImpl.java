package com.wmsi.sgx.service.indexer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.wmsi.sgx.model.indexer.Index;
import com.wmsi.sgx.model.indexer.Indexes;
import com.wmsi.sgx.service.indexer.IndexBuilderService;
import com.wmsi.sgx.service.indexer.IndexQueryResponse;
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
	
	@Value("${elasticsearch.previousDay.index.alias}")
	private String previousDayIndexAlias="sgx_premium_previous";
	
	public String indexName;
	
	@Autowired
	private IndexBuilderService indexBuilderService;
	
	private Resource indexMappingResource = new ClassPathResource("META-INF/mappings/elasticsearch/sgx-mapping.json");
	
	private RestTemplate restTemplate;
	public void setRestTemplate(RestTemplate t){
		restTemplate = t;
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
	}
	
	/**
	 * Skeleton for running any ES Query against an endpoint
	 * 
	 * @param endpoint
	 * @return QueryResponse
	 * 
	 * @throws IndexerServiceException
	 */
	@Override
	public IndexQueryResponse query(String endpoint) throws IndexerServiceException {
		
		try{
			endpoint = "/" + indexName + endpoint;
			URI uri = buildUri(endpoint);
			JsonNode res = restTemplate.getForObject(uri, JsonNode.class);
			return new IndexQueryResponse(res);
		}
		catch(Exception e){
			throw new IndexerServiceException(String.format("Executing query for {}", endpoint), e);
		}

	}
	
	/**
	 * Flushes the ES index
	 *
	 * @return boolean
	 * 
	 * @throws IndexerServiceException
	 */
	@Override
	@Transactional
	public synchronized Boolean flush() throws IndexerServiceException {
		
		try{
			
			URI uri = buildUri("/" + indexName + "/_flush");
			JsonNode res = restTemplate.postForObject(uri, null, JsonNode.class);
			
			log.info("Index Flushed");
			
			return true;
			
		}
		catch(Exception e){
			throw new IndexerServiceException("Executing refresh", e);
		}
		
	}
	
	/**
	 * Creates index name from Spring's Header variable indexName
	 * 
	 * @param indexName
	 * @return Boolean
	 * 
	 * @throws IOException
	 * @throws IndexerServiceException
	 */
	@Override
	@Transactional
	public synchronized Boolean createIndex(@Header String indexName) throws IOException, IndexerServiceException{
		
		this.indexName = indexName;
		
		URI indexUri = buildUri("/" + indexName);
		
		log.info("index name " + indexName);
		
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
	private static final String PREVIOUS_DAY_INDEX = "previousDay"; 
	private static final String CURRENT_DAY_INDEX = "currentDay";
	
	/**
	 * Creates IndexAlias from Spring's Header variable indexName
	 * 
	 * @param indexName
	 * @return Boolean
	 * 
	 * @throws IndexerServiceException
	 */
	@Override
	@Transactional		
	public synchronized Boolean createIndexAlias(@Header String indexName) throws IndexerServiceException {
		URI indexUri = buildUri("/_aliases");
		String aliasType= CURRENT_DAY_INDEX;
		JsonNode alias = buildAliasJson(indexName, aliasType);
		
		log.debug("Updating index aliases {}", alias);
			
		int statusCode = postJson(indexUri, alias);
			
		// Check for 200 range response code
		if(statusCode / 100 != 2){	
			throw new IndexerServiceException("Create alias request returned " + statusCode + " http response code. Could not create alias.");
		}
		
		try
		{
			return createPreviousIndexAlias(indexBuilderService.getPreviousDayIndexName(indexName));
			
		}
		catch (UnsupportedOperationException e)
		{
			log.info("previous index not found, this is first index");
		}		
	    return true;
	}
	
	/**
	 * Creates Previous IndexAlias from Spring's Header variable indexName
	 * 
	 * @param indexName
	 * @return Boolean
	 * 
	 * @throws IndexerServiceException
	 */
	public synchronized Boolean createPreviousIndexAlias(String indexName) throws IndexerServiceException {
		URI indexUri = buildUri("/_aliases");
		String aliasType= PREVIOUS_DAY_INDEX;
		JsonNode alias;
		if(indexName.equals(this.indexName)){
			alias = firstTimePreviousIndexGeneration(indexName);
		}else{
			alias = buildAliasJson(indexName, aliasType);
		}
		log.debug("Updating index aliases {}", alias);
			
		int statusCode = postJson(indexUri, alias);
			
		// Check for 200 range response code
		if(statusCode / 100 != 2){	
			throw new IndexerServiceException("Create previous day alias request returned " + statusCode + " http response code. Could not create alias.");
		}		
		
		return true;
	}
	
	/**
	 * Retrieves list of all available indices 
	 * 
	 * @param indexName
	 * @return list of all available indices 
	 * 
	 * @throws IndexerServiceException
	 */
	@Override
	public Indexes getIndexes() throws IndexerServiceException {
		URI indexUri = buildUri("/_aliases");		
		return restTemplate.getForObject(indexUri, Indexes.class);
	}
	
	/**
	 * Saves data into ES
	 * 
	 * @param indexName
	 * @param type of data bucket
	 * @param id 
	 * @return Boolean
	 * 
	 * @throws IndexerServiceException
	 */
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
	
	
	/**
	 * bulk saves data into ES
	 * 
	 * @param indexName
	 * @param type of data bucket to save data in
	 * @return Boolean
	 * 
	 * @throws IndexerServiceException
	 */
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
	
	/**
	 * Deletes ES Index
	 * 
	 * @param indexName
	 * @return Boolean
	 * 
	 * @throws IndexerServiceException
	 */
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
	
	//Retrieves current index name
	public String getIndexName(){
		return this.indexName;
	}

	private URI buildUri(String path) throws IndexerServiceException{
		try{
			return new URI(esUrl + path);
		}
		catch(URISyntaxException e){
			throw new IndexerServiceException("Invalid uri syntax for alaias request.", e);
		}
	}
	
	/**
	 * Utility method to builds JsonNode based on aliasType and indexName
	 * 
	 * @param indexName
	 * @param aliasType
	 * @return JsonNode
	 *
	 */
	private JsonNode buildAliasJson(String indexName, String aliasType){
		ObjectMapper mapper = new ObjectMapper();
		String aliasName = null;
		
		if(aliasType== CURRENT_DAY_INDEX)aliasName= indexName.substring(0,11);
		if(aliasType== PREVIOUS_DAY_INDEX)aliasName= indexName.substring(0,11)+"_previous";
		
		 
		ObjectNode removeAlias = mapper.createObjectNode();
		ObjectNode remove = mapper.createObjectNode();
		
		removeAlias.put("index", indexName.substring(0,11).concat("*"));
		removeAlias.put("alias", aliasName);
		remove.put("remove",  removeAlias);
		
		ObjectNode addAlias = mapper.createObjectNode();
		ObjectNode add = mapper.createObjectNode(); 

		addAlias.put("index", indexName);
		addAlias.put("alias", aliasName);
		add.put("add", addAlias);
		
		ArrayNode actions = mapper.createArrayNode();
		actions.add(remove);
		actions.add(add);
		
		ObjectNode alias = mapper.createObjectNode();
		alias.put("actions",  actions);

		return alias;
	}
	
	/**
	 * Creating PreviousIndex generation for the first time based on indexName 
	 * 
	 * @param indexName
	 * @return JsonNode
	 *
	 */
	private JsonNode firstTimePreviousIndexGeneration(String indexName){
		ObjectMapper mapper = new ObjectMapper();
		String aliasName = indexName.substring(0,11).concat("_previous");
		
		ObjectNode addAlias = mapper.createObjectNode();
		ObjectNode add = mapper.createObjectNode();
		
		addAlias.put("index", indexName);
		addAlias.put("alias", aliasName);
		add.put("add", addAlias);
		
		ArrayNode actions = mapper.createArrayNode();
		actions.add(add);
		
		ObjectNode alias = mapper.createObjectNode();
		alias.put("actions",  actions);
		
		return alias;
	}
	/**
	 * Builds entity text
	 * 
	 */
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
