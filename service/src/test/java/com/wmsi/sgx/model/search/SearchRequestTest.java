package com.wmsi.sgx.model.search;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class SearchRequestTest{

	@Test
	public void testBuildQuery() throws JsonProcessingException, IOException{
		
		SearchRequest req = new SearchRequest();
		
		Criteria mk = new Criteria();
		mk.setField("marketCap");
		mk.setTo(1000.0);
		mk.setFrom(5.0);
	
		Criteria ig = new Criteria();
		ig.setField("industryGroup");
		ig.setValue("Materials");
		
		Criteria id = new Criteria();
		id.setField("industry");
		id.setValue("Chemicals");
		
		List l = new ArrayList();
		l.add(mk);
		l.add(ig);
		l.add(id);
		
		req.setCriteria(l);
		
		
		
	}
}
