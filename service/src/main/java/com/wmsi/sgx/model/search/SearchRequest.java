package com.wmsi.sgx.model.search;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;
import com.wmsi.sgx.util.TemplateUtil;

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
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		
		try{
			ObjectNode oj = (ObjectNode) m.readTree(template.getFile());
			
			ArrayNode must = (ArrayNode)oj.findValue("must");
			
			for(Criteria c : criteria){
				Query q = null;

				if(c.getField().equals("percentChange")){
					ScriptFilter filter = new ScriptFilter();
					filter.setField(c.getField());
					
					List<Param> parms = new ArrayList<Param>();
					Long toDt = fmt.parse(c.getTo().toString()).getTime();
					Long fromDt = fmt.parse(c.getFrom().toString()).getTime();
					Param p = new Param();
					p.setName("to");
					p.setValue(toDt);
					parms.add(p);

					p = new Param();
					p.setName("from");
					p.setValue(fromDt);
					parms.add(p);
					
					p = new Param();
					p.setName("value");
					p.setValue(c.getValue());
					parms.add(p);

					filter.setScript("v1 = ($ in _source.priceHistory if $.date == from); v2 = ($ in _source.priceHistory if $.date == to); return v2.size() > 0 && v1.size() > 0 ? floor(abs((v2[0].value - v1[0].value) * 100)) == value : false");
					filter.setParams(parms);
					
					oj.putPOJO("filter", filter);	
					
					
					Resource r = new ClassPathResource("META-INF/query/elasticsearch/template/scriptFieldPercentChange.json");
					String pctChangeScriptField = FileUtils.readFileToString(r.getFile());
					
					
					Map<String, Object> pctMap = new HashMap<String, Object>();
					pctMap.put("to", toDt);
					pctMap.put("from", fromDt);
					String scriptFields = TemplateUtil.bind(pctChangeScriptField, pctMap);
					oj.put("script_fields", m.readTree(scriptFields));
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
		catch(ParseException e){
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
