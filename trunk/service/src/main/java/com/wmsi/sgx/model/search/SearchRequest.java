package com.wmsi.sgx.model.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;

public class SearchRequest{

	@Valid
	@NotNull
	@Size(min = 1, max = 5, message="Invalid criteria size")
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

				if(c.getField().equals("percentChange")){
					ScriptFilter filter = new ScriptFilter();
					filter.setField(c.getField());
					
					List<Param> parms = new ArrayList<Param>();
					
					Param p = new Param();
					p.setName("to");
					p.setValue(c.getTo());
					parms.add(p);

					p = new Param();
					p.setName("from");
					p.setValue(c.getFrom());
					parms.add(p);
					
					p = new Param();
					p.setName("value");
					p.setValue(c.getValue());
					parms.add(p);

					filter.setScript("v1 = ($ in _source.priceHistory if $.date == from); v2 = ($ in _source.priceHistory if $.date == to); return v2.size() > 0 && v1.size() > 0 ? abs(floor((v2[0].value - v1[0].value) * 100)) == value : false");
					filter.setParams(parms);
					
					oj.putPOJO("filter", filter);	
					continue;
				}

				else if(c.getTo() != null && c.getFrom() != null){
					RangeQuery r = new RangeQuery();
					r.setField(c.getField());
					r.setRange(new Range(Double.valueOf(c.getFrom().toString()), Double.valueOf(c.getTo().toString())));
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
