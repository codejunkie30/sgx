package com.wmsi.sgx.util;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

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

}
