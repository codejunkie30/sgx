package com.wmsi.sgx.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import au.com.bytecode.opencsv.CSVWriter;

public class Util{

	/** Use reflection to determine if the requested field is numeric and can be used
	* for a statistics query.
	*/ 
	@SuppressWarnings("rawtypes")
	public static Boolean isNumberField(Class clz, String name){
		Field field = ReflectionUtils.findField(clz, name);
		
		if(field == null)
			return false;
		
		Class<?> type = field.getType();		
		return type.equals(Number.class) || type.getSuperclass().equals(Number.class);
	}
	
	public static void convertObjectToCSV(List<String[]> val, OutputStream resOs) throws IOException {
		List<String[]> values = val;

		OutputStream buffOs = new BufferedOutputStream(resOs);
		OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);

		CSVWriter writer = new CSVWriter(outputwriter, ',', CSVWriter.DEFAULT_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\r\n");

		try {
			writer.writeAll(values);
			outputwriter.flush();
		}

		finally {
			outputwriter.close();
			writer.close();
		}

	}

}
