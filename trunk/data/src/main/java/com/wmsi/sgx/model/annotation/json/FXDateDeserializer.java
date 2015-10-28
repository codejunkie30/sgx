package com.wmsi.sgx.model.annotation.json;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class FXDateDeserializer extends JsonDeserializer<Date>
{
	
	private static final Logger log = LoggerFactory.getLogger(FXDateDeserializer.class);
	
	public FXDateDeserializer() {}
	
    @Override
    public Date deserialize(JsonParser jsonparser,
            DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {

    	DateFormat FMT = new SimpleDateFormat("yyyy-MM-dd");
    	FMT.setTimeZone(TimeZone.getTimeZone("GMT"));
    	
    	String date = jsonparser.getText();
    	try {
    		Date d = FMT.parse(date);
    		
    		if (!date.equals(FMT.format(d))) {
    			log.info("NOT A MATCH before {}, after {}", date, FMT.format(d));
    		}
    		
    		return d;
    	} 
    	catch(Exception e) { throw new RuntimeException(e);}

    }

}