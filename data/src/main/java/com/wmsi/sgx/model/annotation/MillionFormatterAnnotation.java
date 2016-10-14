package com.wmsi.sgx.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Annotation for Million Formatter
 * Divides number by million
 * before conversion x after conversion will be x/1,000,000
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface MillionFormatterAnnotation {
	
}

