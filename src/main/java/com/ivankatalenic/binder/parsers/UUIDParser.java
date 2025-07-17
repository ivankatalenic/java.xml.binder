package com.ivankatalenic.binder.parsers;

import com.ivankatalenic.binder.BinderException;
import com.ivankatalenic.binder.Parser;
import org.w3c.dom.Node;

import java.lang.annotation.Annotation;
import java.util.UUID;

import static com.ivankatalenic.binder.parsers.Commons.extractString;

public class UUIDParser implements Parser {
	@Override
	public Object parseFromNode(Node srcNode, Annotation[] destAnnotations) throws BinderException {
		final var nodeText = extractString(srcNode);
		try {
			return UUID.fromString(nodeText);
		} catch (Exception e) {
			throw new BinderException("failed to parse UUID from node text \"%s\"".formatted(nodeText));
		}
	}
}
