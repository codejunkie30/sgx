package com.wmsi.sgx.service.sandp.alpha;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.Test;

import com.wmsi.sgx.service.sandp.alpha.impl.AlphaFactorServiceImpl;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;

public class AlphaFactorServiceTest{

	AlphaFactorService service = new AlphaFactorServiceImpl();
	
	@Test
	public void testLoadAlphaFactors() throws CapIQRequestException, IOException{
		Resource f = new ClassPathResource("data/rank_AFLSG_20140311.txt");
		service.loadAlphaFactors(f.getFile());		
	}

}
