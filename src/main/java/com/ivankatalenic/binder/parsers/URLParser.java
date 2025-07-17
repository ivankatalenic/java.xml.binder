package com.ivankatalenic.binder.parsers;

import com.ivankatalenic.binder.BinderException;
import com.ivankatalenic.binder.Parser;
import org.w3c.dom.Node;

import java.lang.annotation.Annotation;
import java.net.URI;

import static com.ivankatalenic.binder.parsers.Commons.extractString;

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
