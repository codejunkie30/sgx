package com.wmsi.sgx.model.search;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomSerializer extends JsonSerializer<Object>{

	public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		
		if(value instanceof RangeQuery){
			RangeQuery q = (RangeQuery) value;
			jgen.writeStartObject();
			jgen.writeObjectFieldStart("range");
			jgen.writeObjectField(q.getField(), q.getRange());
			jgen.writeEndObject();
			jgen.writeEndObject();
		}
		else if(value instanceof TermQuery){
			TermQuery q = (TermQuery) value;
			jgen.writeStartObject();
			jgen.writeObjectFieldStart("term");
			jgen.writeObjectField(q.getField(), q.getValue());
			jgen.writeEndObject();
			jgen.writeEndObject();
		}
		else if(value instanceof TermsQuery){
			TermsQuery q = (TermsQuery) value;
			jgen.writeStartObject();
			jgen.writeObjectFieldStart("terms");
			jgen.writeObjectField("field", q.getField());
			jgen.writeEndObject();
			jgen.writeEndObject();
		}
		else if(value instanceof HistogramQuery){
			HistogramQuery q = (HistogramQuery) value;
			jgen.writeStartObject();
			jgen.writeObjectFieldStart("histogram");
			jgen.writeObjectField("field", q.getField());
			jgen.writeObjectField("script", q.getScript());
			jgen.writeObjectField("interval", q.getInterval());
			jgen.writeEndObject();
			jgen.writeEndObject();
		}
		else if(value instanceof StatsQuery){
			StatsQuery q = (StatsQuery) value;
			jgen.writeStartObject();
			jgen.writeObjectFieldStart("stats");
			jgen.writeObjectField("field", q.getField());
			jgen.writeEndObject();
			jgen.writeEndObject();
		}
		else if(value instanceof ScriptFilter){
			ScriptFilter s = (ScriptFilter) value;
			jgen.writeStartObject();
			jgen.writeObjectFieldStart("script");
			jgen.writeObjectField("script", s.getScript());
			jgen.writeObjectFieldStart("params");
			for(Param p : s.getParams()){
				jgen.writeObjectField(p.getName(), p.getValue());
			}
			jgen.writeEndObject();
			jgen.writeEndObject();
			jgen.writeEndObject();
		}		
	}
}