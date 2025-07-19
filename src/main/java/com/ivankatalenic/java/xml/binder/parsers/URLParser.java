package com.ivankatalenic.java.xml.binder.parsers;

import com.ivankatalenic.java.xml.binder.BinderException;
import com.ivankatalenic.java.xml.binder.Parser;
import org.w3c.dom.Node;

import java.lang.annotation.Annotation;
import java.net.URI;

import static com.ivankatalenic.java.xml.binder.parsers.Commons.extractString;

public class URLParser implements Parser {
	@Override
	public Object parseFromNode(Node srcNode, Annotation[] destAnnotations) throws BinderException {
		final var nodeText = extractString(srcNode);
		try {
			return new URI(nodeText).toURL();
		} catch (Exception e) {
			throw new BinderException("failed to parse a URL from a node text \"%s\"".formatted(nodeText), e);
		}
	}
}
