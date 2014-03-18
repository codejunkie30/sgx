package com.wmsi.sgx.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestExecutor;
import com.wmsi.sgx.service.search.Search;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.impl.SearchServiceImpl;

@Configuration
@PropertySources(value = {
	@PropertySource("classpath:META-INF/properties/capiq.properties")
})
public class HttpConfig{
	
	@Autowired
	public Environment capIQEnv;

    @Bean(name="capIqJsonMessageConverter")
    public MappingJackson2HttpMessageConverter capIqJsonConverter() {
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	
    	MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
    	jackson.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
    	jackson.setObjectMapper(mapper);
    	
    	return jackson;
    }
    
	@Bean(name = "capIqRestTemplate")	
	public RestTemplate capIqRestTemplate() {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(
						capIQEnv.getProperty("capiq.api.username"),
						capIQEnv.getProperty("capiq.api.password")));
		
		HttpClient httpClient = HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider)
				.setConnectionManager(new PoolingHttpClientConnectionManager())
				.build();
		
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new FormHttpMessageConverter());
		converters.add(capIqJsonConverter());
		template.setMessageConverters(converters);
		
		return template;
	}
	
	@Bean
	public CapIQRequestExecutor capIqRequestExecutor(){
		CapIQRequestExecutor executor = new CapIQRequestExecutor();
		executor.setRestTemplate(capIqRestTemplate());
		executor.setUrl(capIQEnv.getProperty("capiq.api.url"));
		executor.setUserId(capIQEnv.getProperty("capiq.api.username"));
		return executor;
	}

	@Bean(name="esObjectMapper")
	public ObjectMapper esObjectMapper(){
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	return mapper;
	}
	
    @Bean(name="esJsonConverter")
    public MappingJackson2HttpMessageConverter esJsonConverter() {
    	MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
    	jackson.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
    	jackson.setObjectMapper(esObjectMapper());
    	
    	return jackson;
    }
    
	@Bean(name = "esRestTemplate")	
	public RestTemplate esRestTemplate() {
		HttpClient httpClient = HttpClients.custom()
				.setConnectionManager(new PoolingHttpClientConnectionManager())
				.build();
		
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(esJsonConverter());		
		template.setMessageConverters(converters);
		
		return template;
	}
	
	@Bean
	public SearchService companySearchService(){
		SearchServiceImpl serv = new SearchServiceImpl();
		serv.setIndexName("sgx_test");
		serv.setType("company");
		return serv;
	}
	
	@Bean
	public Search<KeyDevs> keyDevsSearch() throws IOException{
		Search<KeyDevs>  s = new Search<KeyDevs>();
		s.setIndexName("sgx_test");
		s.setType("keyDevs");
		s.setResultClass(KeyDevs.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/keyDevs.json");
		s.setQuery(r.getFile());		
		return s;
	}
	
	@Bean
	public Search<Holders> holdersSearch() throws IOException{
		Search<Holders>  s = new Search<Holders>();
		s.setIndexName("sgx_test");
		s.setType("holders");
		s.setResultClass(Holders.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/holders.json");
		s.setQuery(r.getFile());		
		return s;
	}
	
	@Bean
	public Search<CompanyFinancial> financialSearch() throws IOException{
		Search<CompanyFinancial>  s = new Search<CompanyFinancial>();
		s.setIndexName("sgx_test");
		s.setType("financial");
		s.setResultClass(CompanyFinancial.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/financials.json");
		s.setQuery(r.getFile());		
		return s;	
	}
	
	@Bean
	public Search<HistoricalValue> priceSearch() throws IOException{
		Search<HistoricalValue>  s = new Search<HistoricalValue>();
		s.setIndexName("sgx_test");
		s.setType("price");
		s.setResultClass(HistoricalValue.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/historical.json");
		s.setQuery(r.getFile());		
		return s;				
	}

	@Bean
	public Search<HistoricalValue> volumeSearch() throws IOException{
		Search<HistoricalValue>  s = new Search<HistoricalValue>();
		s.setIndexName("sgx_test");
		s.setType("volume");
		s.setResultClass(HistoricalValue.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/historical.json");
		s.setQuery(r.getFile());		
		return s;			
	}

	@Bean
	public Search<AlphaFactor> alphaFactorSearch() throws IOException{
		Search<AlphaFactor>  s = new Search<AlphaFactor>();
		s.setIndexName("sgx_test");
		s.setType("alphaFactor");
		s.setResultClass(AlphaFactor.class);
		Resource r = new ClassPathResource("META-INF/query/elasticsearch/alphaFactor.json");
		s.setQuery(r.getFile());		
		return s;			
	}
}
