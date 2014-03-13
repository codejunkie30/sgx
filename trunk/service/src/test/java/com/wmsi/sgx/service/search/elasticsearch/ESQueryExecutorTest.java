package com.wmsi.sgx.service.search.elasticsearch;

import static org.testng.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.AppConfig;
import com.wmsi.sgx.config.HttpConfig;
import com.wmsi.sgx.model.CompanyInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class ESQueryExecutorTest extends AbstractTestNGSpringContextTests{

	@Configuration
	@ComponentScan(basePackageClasses = {ESQueryExecutor.class})
	@Import(value={HttpConfig.class, AppConfig.class})	
	static class ESQueryExecutorTestConfig{}
	
	@Autowired
	ESQueryExecutor eSQueryExecutor;

	@Test(groups={"functional", "integration"})
	public void testSearch() throws ElasticSearchException {
		SearchQuery query = new SearchQuery();
		query.setQueryTemplate("{ \"query\":{ \"match_all\":{} } }");
		ESResponse response = eSQueryExecutor.executeQuery(query);
		assertNotNull(response);
	}

	@Test(groups={"functional", "integration"})
	public void testGet() throws ElasticSearchException {
		SourceQuery query = new SourceQuery("A7S");
		query.setIndex("sgx_test");
		query.setType("company");
		
		CompanyInfo response = eSQueryExecutor.executeGet(query, CompanyInfo.class);
		assertNotNull(response);
		assertEquals(response.getClass(), CompanyInfo.class);
		assertEquals(response.getTickerCode(), "A7S");
	}


}
