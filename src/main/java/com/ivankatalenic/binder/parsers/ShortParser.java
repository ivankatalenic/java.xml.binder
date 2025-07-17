package com.ivankatalenic.binder.parsers;

import com.ivankatalenic.binder.BinderException;
import com.ivankatalenic.binder.Parser;
import org.w3c.dom.Node;

import java.lang.annotation.Annotation;

import static com.ivankatalenic.binder.parsers.Commons.calculateNodeLocation;
import static com.ivankatalenic.binder.parsers.Commons.extractString;

public class ShortParser implements Parser {
	@Override
	public Object parseFromNode(Node srcNode, Annotation[] destAnnotations) throws BinderException {
		try {
			final var text = extractString(srcNode);
			return Short.parseShort(text);
		} catch (Exception e) {
			throw new BinderException("failed to parse an Short from a node \"%s\"".formatted(calculateNodeLocation(srcNode)), e);
		}
	}
}
