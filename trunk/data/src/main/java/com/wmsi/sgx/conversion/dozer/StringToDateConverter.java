package com.wmsi.sgx.conversion.dozer;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
	
	private static final String[] DEFAULT_DATE_FORMATS = new String[]{ "yyyy-MM-dd", "MMM dd yyyy hh:mma", "MM/dd/yyyy hh:mm:ss"};
	
	@Override
	public Object convert(Object destValue, Object srcValue, Class<?> destClass, Class<?> srcClass) {
		
		Date date = null;
		
		try{
			String[] fmts = DEFAULT_DATE_FORMATS;
			
			if(StringUtils.isNotEmpty(dateFormat)){
				fmts = new String[]{dateFormat};
			}
				
			date = DateUtils.parseDate((String) srcValue, fmts);		
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
