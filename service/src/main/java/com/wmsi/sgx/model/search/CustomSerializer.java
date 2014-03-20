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
	}
}