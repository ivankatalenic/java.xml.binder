package com.ivankatalenic.binder.parsers;

import com.ivankatalenic.binder.BinderException;
import com.ivankatalenic.binder.Parser;
import org.w3c.dom.Node;

import java.lang.annotation.Annotation;

import static com.ivankatalenic.binder.parsers.Commons.extractString;

public class StringParser implements Parser {
	@Override
	public Object parseFromNode(Node srcNode, Annotation[] destAnnotations) throws BinderException {
		return extractString(srcNode);
	}
}
