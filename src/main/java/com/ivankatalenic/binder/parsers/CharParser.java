package com.ivankatalenic.binder.parsers;

import com.ivankatalenic.binder.BinderException;
import com.ivankatalenic.binder.Parser;
import org.w3c.dom.Node;

import java.lang.annotation.Annotation;

import static com.ivankatalenic.binder.parsers.Commons.calculateNodeLocation;

public class CharParser implements Parser {
	@Override
	public Object parseFromNode(Node srcNode, Annotation[] destAnnotations) throws BinderException {
		try {
			final var text = srcNode.getTextContent();
			return text.charAt(0);
		} catch (Exception e) {
			throw new BinderException("failed to parse an Character from a node \"%s\"".formatted(calculateNodeLocation(srcNode)), e);
		}
	}
}
