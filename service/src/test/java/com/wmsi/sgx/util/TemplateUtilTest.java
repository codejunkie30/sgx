package com.wmsi.sgx.util;

import static org.testng.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

public class TemplateUtilTest{

	@Test
	public void testBind(){	
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("f", "template");
		String bound = TemplateUtil.bind("This is a $f$ test", ctx);
		assertEquals("This is a template test", bound);		
	}

	@Test
	public void testNullParms(){	
		String bound = TemplateUtil.bind("This is a $f$ test", null);
		assertEquals("This is a $f$ test", bound);		
	}

}
