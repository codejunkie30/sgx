package com.wmsi.sgx.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
public class JsonDateSerializer extends JsonSerializer<Date> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	@Override
	public void serialize(Date arg0, JsonGenerator arg1, SerializerProvider arg2)
			throws IOException, JsonProcessingException {
		String formattedDate = dateFormat.format(arg0);
		arg1.writeString(formattedDate);
	}

}
