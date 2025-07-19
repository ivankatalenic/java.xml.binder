package com.ivankatalenic.java.xml.binder;

import org.w3c.dom.Node;

import java.lang.annotation.Annotation;

public interface Parser {
	/**
	 * Parses the given type from the {@code srcNode}.
	 *
	 * @param srcNode         An XML element or attribute node from which to extract information necessary for parsing the type instance.
	 * @param destAnnotations A list of annotations placed on the destination field that will take in the parsed type instance.
	 * @return An instance of the given type.
	 * @throws BinderException Thrown when an instance of the given type cannot be parsed from the given {@code srcNode}
	 */
	Object parseFromNode(Node srcNode, Annotation[] destAnnotations) throws BinderException;
}
