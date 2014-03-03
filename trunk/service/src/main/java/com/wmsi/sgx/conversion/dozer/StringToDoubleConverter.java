package com.wmsi.sgx.conversion.dozer;

import org.apache.commons.lang3.math.NumberUtils;
import org.dozer.CustomConverter;

/**
 *  Custom dozer converter to validate String's are proper number values and 
 *  prevent number format exceptions for non numeric strings. 
 */
public class StringToDoubleConverter implements CustomConverter{

	@Override
	public Object convert(Object destValue, Object srcValue, Class<?> destClass, Class<?> srcClass) {
		
		if(srcClass.equals(String.class) && destClass.isAssignableFrom(Double.class)){
			String value = (String) srcValue;
			
			if(NumberUtils.isNumber(value)){
				return Double.valueOf(value);
			}
		}
		
		return null;
	}

	 

}
