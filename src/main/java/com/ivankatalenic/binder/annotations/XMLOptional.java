package com.ivankatalenic.binder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the given field doesn't need to be always present in the XML document.
 * If the field isn't present in the XML document, there won't be an error,
 * and the field will be set to <code>null</code>.
 */
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface XMLOptional {
}
