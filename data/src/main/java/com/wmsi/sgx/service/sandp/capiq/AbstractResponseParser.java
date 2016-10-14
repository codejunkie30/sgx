package com.wmsi.sgx.service.sandp.capiq;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsi.sgx.model.annotation.ConversionAnnotation;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;

public abstract class AbstractResponseParser implements ResponseParser{
	
	private static final Logger log = LoggerFactory.getLogger(AbstractResponseParser.class);
	
	private Map<String, Field> annotedFields;
	private static final String[] DEFAULT_DATE_FORMATS = new String[]{ "yyyy-MM-dd", "MMM dd yyyy hh:mma", "MM/dd/yyyy hh:mm:ss"};
	
	@Override
	public abstract <T> Class<T> getType();
	
	public abstract <T> T convert(CapIQResponse response) throws ResponseParserException;
	
	/**
	 * get Annotated Fields 
	 * @param 
	 * @return annotedFields
	 * 
	 */
	protected Map<String, Field> getAnnotedFields() {
		if(annotedFields == null)
			annotedFields = getFields(getType());

		return annotedFields;
	}
	/**
	 * Utility method to get field values 
	 * @param class type
	 * @return Map<String, Field>
	 * 
	 */
	protected Map<String, Field> getFields(Class<?> clz) {
		Field[] fields = clz.getDeclaredFields();

		Map<String, Field> annoted = new HashMap<String, Field>();

		for(Field f : fields){
			if(f.isAnnotationPresent(ConversionAnnotation.class)){
				ConversionAnnotation annotation = f.getAnnotation(ConversionAnnotation.class);
				annoted.put(annotation.name(), f);
			}
		}

		return annoted;
	}
	
	/**
	 * Parse results coming form CapIQResult 
	 * @param CapIQResult
	 * @param destination object
	 * @return 
	 * @throws ResponseParserException
	 */
	protected void parseResult(CapIQResult result, Object dest) throws ResponseParserException {
		Map<String, Field> fields = getAnnotedFields();

		try{
			String mnemonic = result.getMnemonic();
			Field f = fields.get(mnemonic);

			if(f == null)
				return;

			String value = getResultValue(result);

			if(value != null){

				Object obj = convertValue(value, f);

				if(obj != null)
					BeanUtils.setProperty(dest, f.getName(), obj);
			}
		}
		catch(IllegalAccessException | InvocationTargetException | ParseException e){
			throw new ResponseParserException("Couldn't parse results", e);
		}
	}
	
	/**
	 * Utility method to get CapIQResult values 
	 * @param CapIQResult
	 * @return result value
	 * @throws ResponseParserException
	 */
	protected String getResultValue(CapIQResult res) throws ResponseParserException{
		List<String> values = getResultValues(res);
		
		String value = null;
		if(values != null && !values.isEmpty()){
			
			String val = values.get(0);
			
			if(val != null && !val.toLowerCase().startsWith("data unavailable")){
				value = val;
			}
		}
		
		return value;
	}
	
	/**
	 * Utility method to get list of CapIQResult values 
	 * @param CapIQResult
	 * @return list of result value
	 * @throws ResponseParserException
	 */
	protected List<String> getResultValues(CapIQResult res) throws ResponseParserException{
		String err = res.getErrorMsg();

		if(StringUtils.isNotEmpty(err)){
			log.error("Error response {}", err);
			
			if(err.equalsIgnoreCase("invalididentifier"))
				throw new InvalidIdentifierException("Invalid Identifier " + res.getIdentifier());
			
			throw new ResponseParserException("Error result in capIq request " + err);
		}

		List<String> values = new ArrayList<String>();

		if(res.getRows() != null && !res.getRows().isEmpty()){
			
			for(CapIQRow row : res.getRows()){
				String str = row.getValues().get(0);

				if(StringUtils.isNotEmpty(str) && !str.toLowerCase().startsWith("data unavailable")){
					values.add(str);
				}
			}
		}

		return values;
	}
	
	/**
	 * Utility method to convert a result values based on field 
	 * @param value to be converted
	 * @param value to be converted based on field
	 * @return converted result value
	 * @throws ParseException
	 */
	protected Object convertValue(String value, Field f) throws ParseException {
	
		if(value != null && value.equalsIgnoreCase("nan"))
			return null;
	
		Object obj = value;
		
		if(f.getType().equals(Date.class)){
			// Convert dates
			obj = DateUtils.parseDate(value, DEFAULT_DATE_FORMATS);	
		}

		return obj;
	}

}
