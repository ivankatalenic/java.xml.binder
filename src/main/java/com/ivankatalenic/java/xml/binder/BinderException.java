package com.ivankatalenic.java.xml.binder;

/**
 * An exception that's thrown when some type cannot be parsed from a DOM node.
 */
public class BinderException extends Exception {
	public BinderException(String message) {
		super(message);
	}

	public BinderException(String message, Throwable cause) {
		super(message, cause);
	}
}
