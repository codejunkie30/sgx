package com.wmsi.sgx.model.validation.annotations;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsi.sgx.model.search.Criteria;

public class CriteriaSizeValidator implements ConstraintValidator<CriteriaSize, List<Criteria>> {

	private static final Logger log = LoggerFactory.getLogger(CriteriaSizeValidator.class);

	private CriteriaSize annot;
	
	@Override
	public void initialize(CriteriaSize a){
		annot = a;
	}	 
	
	@Override
	public boolean isValid(List<Criteria> criteria, ConstraintValidatorContext ctx) {
		
		log.debug("Validating max criteria within range {}-{}", annot.min(), annot.max());
		
		int total = 0;
		
		if(criteria != null){
			for(Criteria c : criteria){
				
				// Check if field should be included in validation total. 
				if(includeField(c.getField())){
					total++;
				}
			}
		}
		
		boolean valid = total >= annot.min() && total <= annot.max();
		
		log.debug("Validation = {}, {} fields included in validation", valid, total);		
		
		return valid;
		
	}
	
	private boolean includeField(String field){
		
		for(String ignored : annot.ignoreFields()){
			
			if(ignored.equalsIgnoreCase(field)){
				log.debug("Excluded field '{}' from validation total.", field);
				return false;
			}
		}
		
		return true;
	}
}
