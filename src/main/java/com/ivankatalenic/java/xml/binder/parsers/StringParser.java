package com.ivankatalenic.java.xml.binder.parsers;

import com.ivankatalenic.java.xml.binder.BinderException;
import com.ivankatalenic.java.xml.binder.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import java.lang.annotation.Annotation;

import static com.ivankatalenic.java.xml.binder.parsers.Commons.calculateNodeLocation;
import static com.ivankatalenic.java.xml.binder.parsers.Commons.extractString;

public class StringParser implements Parser {
	@Override
	public Object parseFromNode(Node srcNode, Annotation[] destAnnotations) throws BinderException {
		try {
			return srcNode.getTextContent();
		} catch (Exception e) {
			throw new BinderException("failed to extract a string from a node \"%s\"".formatted(calculateNodeLocation(srcNode)), e);
		}
	}
}
