package com.wmsi.sgx.service.search.elasticsearch;

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

import com.wmsi.sgx.config.HttpConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class ESQueryExecutorTest extends AbstractTestNGSpringContextTests{

	@Configuration
	@ComponentScan(basePackageClasses = {ESQueryExecutor.class})
	@Import(HttpConfig.class)
	static class ESQueryExecutorTestConfig{}
	

	@Autowired
	ESQueryExecutor eSQueryExecutor;

	@Test
	public void testCreateIndex() throws ElasticSearchException {
		ESQuery query = new ESQuery();
		query.setQueryTemplate("{ \"query\":{ \"match_all\":{} } }");
		ESResponse response = eSQueryExecutor.executeQuery(query);
		System.out.println(response.toString());
	}
	
}
