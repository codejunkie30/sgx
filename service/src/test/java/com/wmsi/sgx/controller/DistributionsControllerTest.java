package com.wmsi.sgx.controller;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.wmsi.sgx.model.distribution.DistributionBucketBuilder;
import com.wmsi.sgx.model.distribution.DistributionBuilder;
import com.wmsi.sgx.model.distribution.DistributionsBuilder;
import com.wmsi.sgx.model.distribution.DistributionsRequest;
import com.wmsi.sgx.service.DistributionService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class DistributionsControllerTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private DistributionService distributionService;
	
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetDistributions() throws Exception{

		when(distributionService.getAggregations(any(DistributionsRequest.class)))
			.thenReturn(
				DistributionsBuilder.distributions()
				.withAddedDistribution(
					DistributionBuilder.distribution()
					.withField("marketCap")
					.withAddedBucket(
							DistributionBucketBuilder.distributionBucket()
							.withCount(24L)
							.withFrom("0")
							.withTo("500")
							.build())
					.build())					
				.build());
		

		mockMvc.perform(post("/search/distributions")
				.content("{\"fields\":[\"marketCap\"]}")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.distributions", Matchers.hasSize(1)))
			.andExpect(jsonPath("$.distributions[0].field", Matchers.is("marketCap")))
			.andExpect(jsonPath("$.distributions[0].buckets", Matchers.hasSize(1)))
			.andExpect(jsonPath("$.distributions[0].buckets[0].from", Matchers.is("0")))
			.andExpect(jsonPath("$.distributions[0].buckets[0].to", Matchers.is("500")));
	}

	@Test
	public void testMethodsNotAllowed() throws Exception{

		mockMvc.perform(get("/search/distributions")
				.content("{}")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isMethodNotAllowed());

		mockMvc.perform(put("/search/distributions")
				.content("{}")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isMethodNotAllowed());

		mockMvc.perform(delete("/search/distributions")
				.content("{}")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isMethodNotAllowed());
	}

	@Test(dataProvider="validFieldsJson")
	public void testSearchCompany_SuccessfulValidation(String json) throws Exception{
		mockMvc.perform(post("/search/distributions")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test(dataProvider="invalidFieldsJson")
	public void testSearchCompany_FailValidation(String json) throws Exception{
		mockMvc.perform(post("/search/distributions")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Configuration
	static class DistributionsControllerTestConfig{

		@Bean
		public DistributionsController distributionsController(){
			return new DistributionsController();
		}
		
		@Bean
		public DistributionService distributionService(){
			return mock(DistributionService.class);
		}		
 
		@Bean
		public MockMvc mockMvc(){			
			return MockMvcBuilders.standaloneSetup(distributionsController()).build();
		}
	}

	@DataProvider
	public Object[][] validFieldsJson(){
		return new String[][]{
			{"{\"fields\":[\"marketCap\"] }"},			
			{"{\"fields\":[\"marketCap\",\"beta5Yr\",\"avgBrokerReq\",\"dividendYield\",\"ebitdaMargin\"] }"}
		};
	}	

	@DataProvider
	public Object[][] invalidFieldsJson(){
		return new String[][]{
			{""},
			{"{}"},
			{"{\"fields\":\"wrongType\"}"},
			{"{\"fields\":{}\"}"},
			{"{\"fields\":[]\"}"},
			{"{\"fields\":[\"badField\"] }"},			
			{"{\"fields\":[\"tooMany\",\"2\",\"3\",\"4\",\"5\",\"6\"] }"}
		};
	}	

}
