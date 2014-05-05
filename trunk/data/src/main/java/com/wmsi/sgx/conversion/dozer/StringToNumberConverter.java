package com.wmsi.sgx.conversion.dozer;

import org.apache.commons.lang3.math.NumberUtils;
import org.dozer.CustomConverter;

/**
 *  Custom dozer converter to validate String's are proper number values and 
 *  prevent number format exceptions for non numeric strings. 
 */
public class StringToNumberConverter implements CustomConverter{

	@Override
	public Object convert(Object destValue, Object srcValue, Class<?> destClass, Class<?> srcClass) {
		
		Number num = null;
		
		if(srcClass.equals(String.class) && Number.class.isAssignableFrom(destClass)){
			String value = (String) srcValue;
			
			if(!NumberUtils.isNumber(value)){
				return null;
			}
			
			if(destClass.equals(Double.class)){
				num = Double.valueOf(value);
			}
			else if(destClass.equals(Integer.class)){
				num = Integer.valueOf(value);
			}
			else if(destClass.equals(Long.class)){
				num = Long.valueOf(value);
			}
			else if(destClass.equals(Float.class)){
				num = Float.valueOf(value);
			}
		}
		
		return num;
	}
}
