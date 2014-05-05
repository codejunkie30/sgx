package com.wmsi.sgx.conversion.dozer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.CustomConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;

public class ResponseToObjectConverter implements CustomConverter, MapperAware{

	private static final Logger log = LoggerFactory.getLogger(ResponseToObjectConverter.class);
	
	private Mapper mapper;

	@Override
	public Object convert(Object destValue, Object srcValue, Class<?> destClass, Class<?> srcClass) {

		CapIQResponse response = (CapIQResponse) srcValue;

		Object dest = null;

		try{
			dest = destClass.newInstance();
			Map<String, Field> annotedFields = getFields(destClass);

			for(CapIQResult result : response.getResults()){
				String mnemonic = result.getMnemonic();
				Field f = annotedFields.get(mnemonic);

				if(f == null)
					continue;

				String value = getResultValue(result);

				if(value != null){

					Object obj = convertValue(value, f); 
					
					if(obj != null)
						BeanUtils.setProperty(dest, f.getName(), obj);
				}
			}
		}
		catch(IllegalArgumentException | IllegalAccessException | ConverterException | InstantiationException
				| InvocationTargetException | ParseException e){
			throw new ConverterException("Couldn't set field", e);
		}

		return dest;
	}
	
	private Object convertValue(String value, Field f) throws ParseException{
		Object obj = value; 
		
		if(!f.getType().equals(String.class))
			// Convert non string types
			obj = mapper.map(value, f.getType());

		return obj;
	}

	private String getResultValue(CapIQResult res) throws ConverterException {
		String err = res.getErrorMsg();

		if(StringUtils.isNotEmpty(err)){
			log.error("Error response {}", err);
			throw new ConverterException("Error " + err, err);
		}

		String val = null;

		if(res.getRows() != null && !res.getRows().isEmpty()){

			List<String> values = res.getRows().get(0).getValues();

			if(values != null && !values.isEmpty())
				val = values.get(0);
		}

		if(val != null && val.toLowerCase().startsWith("data unavailable")){
			return null;
		}

		return val;
	}

	private Map<String, Field> getFields(Class<?> clz) {
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

	@Override
	public void setMapper(Mapper m) {
		mapper = m;
	}
}
