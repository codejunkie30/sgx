package com.wmsi.sgx.service.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.service.search.elasticsearch.ESQuery;
import com.wmsi.sgx.service.search.elasticsearch.ESQueryExecutor;
import com.wmsi.sgx.service.search.elasticsearch.ESResponse;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.SearchQuery;

@Service
public class CompanyInfoService{

	@Autowired
	private ESQueryExecutor esExecutor;

	private static final String COMPANY_INDEX = "company";	
	private Resource template = new ClassPathResource("META-INF/query/elasticsearch/companyInfo.json");
	
	private CompanyInfo loadCompany(String id) throws ElasticSearchException{
		ESQuery query = new SearchQuery();
		query.setIndex(COMPANY_INDEX);
		
		Map<String,String> m = new HashMap<String, String>();
		m.put("id", id);
		
		try{
			query.setQueryTemplate(parseQuery(template, m));
		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		ESResponse response = esExecutor.executeQuery(query);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		response.setObjectMapper(mapper);
		return response.getHits(CompanyInfo.class).get(0);
	}
	
	public String parseQuery(Resource template, Map<String, String> ctx) throws IOException{
		String query = null;
		
		String queryTemplate = FileUtils.readFileToString(template.getFile());
		StringTemplate st = new StringTemplate(queryTemplate);
			
		Iterator<Entry<String, String>> i = ctx.entrySet().iterator();
			
		while(i.hasNext()){
			Entry<String, String> entry = i.next();
			st.setAttribute(entry.getKey(), entry.getValue());
		}
			
		query = st.toString();
		
		return query;
	}


}
