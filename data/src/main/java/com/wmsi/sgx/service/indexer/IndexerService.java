package com.wmsi.sgx.service.indexer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.integration.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class IndexerService{

	private Logger log = LoggerFactory.getLogger(IndexerService.class);
	
	@Value("${elasticsearch.url}")
	private String esUrl;

	private String indexNamePrefix = "sgx_";
	
	private Resource indexMappingResource = new ClassPathResource("META-INF/mappings/elasticsearch/sgx-mapping.json");
	
	@Autowired	
	@Qualifier("esRestTemplate")
	private RestTemplate restTemplate;
	public void setRestTemplate(RestTemplate t){restTemplate = t;}
	
	public synchronized void createIndex(@Header String asOfDate) throws IOException, IndexerServiceException{
		String indexName = indexNamePrefix + asOfDate;
		URI indexUri = buildUri("/" + indexName);
		
		ClientHttpResponse headResponse = restTemplate.getRequestFactory().createRequest(indexUri, HttpMethod.HEAD).execute();
		boolean indexExists = headResponse.getStatusCode() == HttpStatus.OK;
		
		if(indexExists){
			log.debug("Index exists " + indexExists);
			return;
		}
			
		ObjectMapper mapper = new ObjectMapper();
		JsonNode readTree = mapper.readTree(indexMappingResource.getInputStream());

		int statusCode = postJson(indexUri, readTree);
		
		// Check for 200 range response code
		if(statusCode / 100 != 2){			
			throw new IndexerServiceException("Create index request returned " + statusCode + " http response code. Could not create index");
		}		
	}
	
	public synchronized void createIndexAlias(@Header String indexName) throws IndexerServiceException {
		
		URI indexUri = buildUri("/_aliases");
		JsonNode alias = buildAliasJson( indexNamePrefix + indexName);
		
		log.debug("Updating index aliases {}", alias);
			
		int statusCode = postJson(indexUri, alias);
			
		// Check for 200 range response code
		if(statusCode / 100 != 2){	
			throw new IndexerServiceException("Create alaias request returned " + statusCode + " http response code. Could not create alias.");
		}		
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
		
		removeAlias.put("index", "sgx_*");
		removeAlias.put("alias", "sgx");
		remove.put("remove",  removeAlias);
		
		ObjectNode addAlias = mapper.createObjectNode();
		ObjectNode add = mapper.createObjectNode(); 

		addAlias.put("index", indexName);
		addAlias.put("alias", "sgx");
		add.put("add", addAlias);
		
		ArrayNode actions = mapper.createArrayNode();
		actions.add(remove);
		actions.add(add);
		
		ObjectNode alias = mapper.createObjectNode();
		alias.put("actions",  actions);

		return alias;
	}


	private <T> HttpEntity<T> buildEntity(T json){
		HttpHeaders headers = new HttpHeaders();
		Charset utf8 = Charset.forName("UTF-8");
		MediaType mediaType = new MediaType("application", "json", utf8);
		headers.setContentType(mediaType);
		headers.setAcceptCharset(Collections.singletonList(utf8));
		return new HttpEntity<T>(json, headers);		
	}
	
	private int postJson(URI uri, Object json){
		HttpEntity<?> entity = buildEntity(json);
		ResponseEntity<Object> res = restTemplate.exchange(uri, HttpMethod.POST, entity, Object.class);		
		return res.getStatusCode().value();		
	}

	@Transactional
	public void save(String type, String id, Object companyInfo, String asOfDate) {
        String indexName = indexNamePrefix + asOfDate;
        
        UriComponents uriComp = UriComponentsBuilder.fromUriString(esUrl + "/{indexName}/{type}/{id}?opt_type=create").build();
        URI uri = uriComp.expand(indexName, type, id).toUri();
        
        restTemplate.exchange(uri, HttpMethod.PUT, buildEntity(companyInfo), Object.class);
	}
}
