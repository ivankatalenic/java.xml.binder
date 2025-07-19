package com.ivankatalenic.java.xml.binder.parsers;

import com.ivankatalenic.java.xml.binder.BinderException;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class Commons {
	private Commons() {
	}

	public static String extractString(Node node) throws BinderException {
		final var text = node.getTextContent();
		if (text == null) {
			throw new BinderException("failed to extract string from a node \"%s\"".formatted(calculateNodeLocation(node)));
		}
		return text.trim();
	}

	public static String calculateNodeLocation(Node node) {
		if (node instanceof Attr at) {
			return calculateNodeLocation(at.getOwnerElement()) + " Attribute: " + at.getName();
		}
		if (node.getParentNode() == null) {
			return "/";
		}
		return calculateNodeLocation(node.getParentNode()) + "/" + node.getNodeName();
	}
}
