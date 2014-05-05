package com.wmsi.sgx.conversion.dozer;

import java.io.IOException;

import org.dozer.Mapper;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ResponseToObjectConverterTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private Mapper dozerMappingBean;

	@Autowired
	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testMapper() throws JsonParseException, JsonMappingException, IOException {
		Resource json = new ClassPathResource("data/capiq/companyResponse.json");
		CapIQResponse response = mapper.readValue(json.getInputStream(), CapIQResponse.class);

		Company company = dozerMappingBean.map(response, Company.class);
		System.out.println(company);
		//System.out.println(response);
	}

	@Configuration
	static class ResponseToObjectConverterTestConfig{

		@Bean
		public DozerBeanMapperFactoryBean dozerMappingBean() throws Exception {

			DozerBeanMapperFactoryBean factory = new DozerBeanMapperFactoryBean();
			factory.setMappingFiles(new PathMatchingResourcePatternResolver()
					.getResources("classpath*:META-INF/mappings/dozer/*.xml"));
			return factory;
		}

		@Bean
		public ObjectMapper mapper() {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return mapper;
		}

	}
}
