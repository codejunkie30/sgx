package com.wmsi.sgx.service.sandp.capiq;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;

@Service
public class CapIQServiceImpl{

	Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	@Autowired
	@Qualifier("capIqRestTemplate")
	private RestTemplate restTemplate;

	// TODO Refactor - proof of concept
	public void getCompanyInfo() throws IOException {
		String query = null;
		Resource resource = new ClassPathResource(
				"META-INF/query/capiq/companyInfo.json");
		String queryTemplate = FileUtils.readFileToString(resource.getFile());
		query = queryTemplate.replaceAll("\\$id", "C6L");

		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application",
				"x-www-form-urlencoded", Charset.forName("UTF-8"));
		headers.setContentType(mediaType);
		headers.setConnection(HTTP.CONN_KEEP_ALIVE);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();

		body.add("inputRequests", query);
		body.add("userId", "APIADMIN@WEALTHMSI.COM");

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(
				body, headers);
		StopWatch w = new StopWatch();
		w.start();
		String url = "https://sdk.gds.standardandpoors.com/gdssdk/rest/v2/clientservice.json";
		ResponseEntity<CapIQResponse> res = restTemplate.exchange(url,
				HttpMethod.POST, entity, CapIQResponse.class);

		w.stop();
		log.error("Time taken: {} ", w.getTotalTimeMillis());
		CapIQResponse response = res.getBody();
		for(CapIQResult val : response.getResults()){
			log.error(val.getRows().get(0).getValues().get(0).toString());
		}
	}
}
