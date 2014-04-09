package com.wmsi.sgx.conversion.dozer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dozer.ConfigurableCustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Custom dozer converter to validate String's are proper number values and 
 *  prevent number format exceptions for non numeric strings. 
 */
public class StringToDateConverter implements ConfigurableCustomConverter{

	private static final Logger log = LoggerFactory.getLogger(StringToDateConverter.class);

	private String dateFormat;
	
	@Override
	public Object convert(Object destValue, Object srcValue, Class<?> destClass, Class<?> srcClass) {
		
		SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
		Date date = null;
		
		try{
			date = fmt.parse((String) srcValue);
		}
		catch(ParseException e){
			log.debug("Unparsable date {}", srcValue);
		}
		
		return date;
	}

	@Override
	public void setParameter(String parm) {
		dateFormat = parm;
	}
}
