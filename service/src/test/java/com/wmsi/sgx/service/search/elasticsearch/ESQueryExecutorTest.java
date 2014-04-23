package com.wmsi.sgx.service.search.elasticsearch;

import static org.testng.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.wmsi.sgx.config.SearchConfig;
import com.wmsi.sgx.model.Company;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class ESQueryExecutorTest extends AbstractTestNGSpringContextTests{

	@Configuration
	@ComponentScan(basePackageClasses = {ESQueryExecutor.class})
	@Import(value={HttpConfig.class, AppConfig.class, SearchConfig.class})	
	static class ESQueryExecutorTestConfig{}
	
	@Autowired
	ESQueryExecutor esExecutor;
	
	@Value("${elasticsearch.index.name}")
	private String indexName;


	@Test(groups={"functional", "integration"})
	public void testSearch() throws ElasticSearchException {
		SearchQuery query = new SearchQuery();
		query.setQueryTemplate("{ \"query\":{ \"match_all\":{} } }");
		ESResponse response = esExecutor.executeQuery(query);
		assertNotNull(response);
	}

	@Test(groups={"functional", "integration"})
	public void testGet() throws ElasticSearchException {
		SourceQuery query = new SourceQuery("A7S");
		query.setIndex(indexName);
		query.setType("company");
		
		Company response = esExecutor.executeGet(query, Company.class);
		assertNotNull(response);
		assertEquals(response.getClass(), Company.class);
		assertEquals(response.getTickerCode(), "A7S");
	}


}
