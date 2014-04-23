package com.wmsi.sgx.controller.search;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.commons.lang3.StringUtils;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmsi.sgx.controller.SearchController;
import com.wmsi.sgx.model.search.CompanySearchRequest;
import com.wmsi.sgx.model.search.CompanySearchRequestBuilder;
import com.wmsi.sgx.model.search.CriteriaBuilder;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchCompanyBuilder;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchRequestBuilder;
import com.wmsi.sgx.model.search.SearchResultsBuilder;
import com.wmsi.sgx.service.CompanySearchService;
import com.wmsi.sgx.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class SearchControllerTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private CompanySearchService companySearchService;

	@Test	
	public void testGetNotAllowed() throws Exception{		
		mockMvc.perform(get("/search/name")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isMethodNotAllowed());
		
		mockMvc.perform(get("/search")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isMethodNotAllowed());
	}
	
	@Test
	public void testSearch() throws Exception{
		SearchCompany company = new SearchCompanyBuilder()
		.withCompanyName("Fab chem China Limited")
		.withBeta5Yr(0.182D)
		.withDividendYield(5.7851D)
		.build();
		
		SearchRequest req = new SearchRequestBuilder()
			.withAddedCriteriaElement(
					new CriteriaBuilder()
					.withField("marketCap")
					.withTo(5000D)
					.withFrom(5D)
					.build())
				.build();
		
		when(companySearchService
				.search(
						any(SearchRequest.class)))
				.thenReturn(
						SearchResultsBuilder.searchResults()
						.withAddedCompany(company)
						.build());

		mockMvc.perform(post("/search")
				.content(TestUtils.objectToJson(req))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.companies", Matchers.hasSize(1)))
			.andExpect(jsonPath("$.companies[0].companyName", Matchers.is("Fab chem China Limited")))
			.andExpect(jsonPath("$.companies[0].beta5Yr", Matchers.is(0.182D)))
			.andExpect(jsonPath("$.companies[0].dividendYield", Matchers.is(5.7851D)));
		
		 verify(companySearchService, times(1)).search(any(SearchRequest.class));
	     verifyNoMoreInteractions(companySearchService);
	}
	
	@Test
	public void testCompanySearchByName() throws Exception{
		
		CompanySearchRequest request = new CompanySearchRequestBuilder()
			.withSearch("China")
			.build();
		
		SearchCompany company = new SearchCompanyBuilder()
			.withCompanyName("Fab chem China Limited")
			.withBeta5Yr(0.182D)
			.withDividendYield(5.7851D)
			.build();
		
		when(companySearchService.searchCompaniesByName(
					any(CompanySearchRequest.class)))
				.thenReturn(
					SearchResultsBuilder.searchResults()
					.withAddedCompany(company)
					.build());
		
		mockMvc.perform(post("/search/name")
				.content(TestUtils.objectToJson(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.companies", Matchers.hasSize(1)))
			.andExpect(jsonPath("$.companies[0].companyName", Matchers.is("Fab chem China Limited")))
			.andExpect(jsonPath("$.companies[0].beta5Yr", Matchers.is(0.182D)))
			.andExpect(jsonPath("$.companies[0].dividendYield", Matchers.is(5.7851D)));
		
		 verify(companySearchService, times(1)).searchCompaniesByName(any(CompanySearchRequest.class));
	     verifyNoMoreInteractions(companySearchService);
	}
	
	@Test(dataProvider="invalidSearchCriteria")
	public void testSearch_FailValidation(String json) throws Exception{
		mockMvc.perform(post("/search")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test(dataProvider="invalidSearchString")
	public void testSearchCompany_FailValidation(String json) throws Exception{
		mockMvc.perform(post("/search/name")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Configuration
	static class SearchControllerTestConfig{

		@Bean
		public SearchController searchController(){
			return new SearchController();
		}
		
		@Bean
		public CompanySearchService companySearchService(){
			return mock(CompanySearchService.class);
		}		
 
		@Bean
		public MockMvc mockMvc(){			
			return MockMvcBuilders.standaloneSetup(searchController()).build();
		}
	}

	@DataProvider
	public Object[][] invalidSearchString(){
		return new String[][]{
			{""},
			{"{}"},
			{"{\"wrongName\":\"Text\"}"},
			{"{\"companyName\":\"<script>alert('foo');</script>\"}"},
			{"{\"companyName\":\"<img src=\"img.jpg\"/>\"}"},
			{"{\"companyName\":\"" + StringUtils.rightPad("longString", 130, '*') + "\"}"}
		};
	}	

	@DataProvider
	public Object[][] invalidSearchCriteria() throws JsonProcessingException, IllegalArgumentException{
		
		CriteriaBuilder b =
				new CriteriaBuilder()
				.withField("doesntMatter");
		
		SearchRequestBuilder tooMuchCriteria = new SearchRequestBuilder();
		
		for(int i = 0; i < 6; i++){
			tooMuchCriteria.withAddedCriteriaElement(b.build());
		}
		
		return new String[][]{
			{""},
			{"{}"},
			{"{\"criteria\":[ {\"field\":\"wrongName\",\"to\":500,\"from\":15} ]}"},
			{TestUtils.objectToJson(requestWithCriteria(6))}
		};
	}	

	private SearchRequest requestWithCriteria(Integer size){
		CriteriaBuilder b =
				new CriteriaBuilder()
				.withField("doesntMatter");
		
		SearchRequestBuilder req = new SearchRequestBuilder();
		
		for(int i = 0; i < size; i++){
			req.withAddedCriteriaElement(b.build());
		}
		
		return req.build();
	}
}
