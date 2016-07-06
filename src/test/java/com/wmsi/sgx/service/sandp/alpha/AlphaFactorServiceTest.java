package com.wmsi.sgx.service.sandp.alpha;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.wmsi.sgx.config.HttpConfig;

@ContextConfiguration(classes={HttpConfig.class})
public class AlphaFactorServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private AlphaFactorIndexerService service;
	
	//@Test(groups="integration")
	public void testGetLatestFile() throws AlphaFactorServiceException, IOException{
		service.getLatestFile();
	}
	
	//@Test(groups="integration")
	public void testLoadAlphaFactors() throws IOException, AlphaFactorServiceException {
		File f = service.getLatestFile();
		service.loadAlphaFactors(f);		
	}

}
