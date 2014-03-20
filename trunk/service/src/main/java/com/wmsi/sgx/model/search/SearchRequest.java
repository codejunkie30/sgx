package com.wmsi.sgx.model.search;

import java.io.IOException;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;

public class SearchRequest{

	private List<Criteria> criteria;
	
	public List<Criteria> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<Criteria> criteria) {
		this.criteria = criteria;
	}

	// TODO Refactor to external query builder class
	public String buildQuery(){
		ObjectMapper m = new ObjectMapper();
		Resource template = new ClassPathResource("META-INF/query/elasticsearch/template/constantScoreBooleanFilter.json");

		try{
			ObjectNode oj = (ObjectNode) m.readTree(template.getFile());
			ArrayNode must = (ArrayNode)oj.findValue("must");
			
			for(Criteria c : criteria){
				Query q = null;
				
				if(c.getTo() != null && c.getFrom() != null){
					RangeQuery r = new RangeQuery();
					r.setField(c.getField());
					r.setRange(new Range(c.getFrom(), c.getTo()));
					q = r;
				}
				else{
					TermQuery r = new TermQuery();
					r.setField(c.getField());
					r.setValue(c.getValue());
					q = r;
				}
				must.add(m.valueToTree(q));
			}
			return m.writeValueAsString(oj);
		}
		catch(JsonProcessingException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("criteria", criteria)
			.toString();
	}
}
