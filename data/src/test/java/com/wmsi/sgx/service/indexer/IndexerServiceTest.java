package com.wmsi.sgx.service.indexer;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.HttpConfig;
import com.wmsi.sgx.model.indexer.Indexes;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={HttpConfig.class})
public class IndexerServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private IndexerService indexerService;
	
	@Test
	public void testGetAllIndexes() throws IndexerServiceException{
		Indexes indexes  = indexerService.getIndexes();
		System.out.println(indexes);
	}
	
}
