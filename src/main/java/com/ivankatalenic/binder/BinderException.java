package com.ivankatalenic.binder;

public class BinderException extends Exception {
	public BinderException(String message) {
		super(message);
	}

	public BinderException(String message, Throwable cause) {
		super(message, cause);
	}
}
