package com.wmsi.sgx.service.sandp.alpha;

import java.io.File;
import java.io.IOException;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.HttpConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={HttpConfig.class})
public class AlphaFactorServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private AlphaFactorIndexerService service;
	
	@Test
	public void testGetLatestFile() throws AlphaFactorServiceException, IOException{
		service.getLatestFile();
	}
	
	@Test
	public void testLoadAlphaFactors() throws IOException, AlphaFactorServiceException {
		File f = service.getLatestFile();
		service.loadAlphaFactors(f);		
	}

}
