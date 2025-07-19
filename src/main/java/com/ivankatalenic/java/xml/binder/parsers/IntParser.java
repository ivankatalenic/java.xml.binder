package com.ivankatalenic.java.xml.binder.parsers;

import com.ivankatalenic.java.xml.binder.BinderException;
import com.ivankatalenic.java.xml.binder.Parser;
import org.w3c.dom.Node;

import java.lang.annotation.Annotation;

import static com.ivankatalenic.java.xml.binder.parsers.Commons.calculateNodeLocation;
import static com.ivankatalenic.java.xml.binder.parsers.Commons.extractString;

public class IntParser implements Parser {
	@Override
	public Object parseFromNode(Node srcNode, Annotation[] destAnnotations) throws BinderException {
		try {
			final var text = extractString(srcNode);
			return Integer.parseInt(text);
		} catch (Exception e) {
			throw new BinderException("failed to parse an Integer from a node \"%s\"".formatted(calculateNodeLocation(srcNode)), e);
		}
	}
}
