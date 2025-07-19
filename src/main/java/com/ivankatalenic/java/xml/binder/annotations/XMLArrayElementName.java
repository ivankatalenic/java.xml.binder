package com.ivankatalenic.java.xml.binder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When placed on a field with an array type,
 * it specifies the element's name from which to parse the individual array element.
 */
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface XMLArrayElementName {
	String value();
}
