package com.ivankatalenic.java.xml.binder;

import com.ivankatalenic.java.xml.binder.annotations.*;
import com.ivankatalenic.java.xml.binder.parsers.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.net.URL;
import java.util.*;

/**
 * Binder is used for binding an XML document (represented by a DOM tree) to a user-defined class,
 * possibly supplemented with annotations on the class' fields that guide the binding process.
 */
public class Binder {
	private final XPath xpath;
	private final Map<Class<?>, Parser> parsers;

	public Binder() {
		xpath = XPathFactory.newInstance().newXPath();
		parsers = new HashMap<>();
		setUpDefaultParsers();
	}

	static <T extends Record> Constructor<T> getRecordCanonicalConstructor(Class<T> recClass) throws BinderException {
		Class<?>[] paramTypes =
				Arrays.stream(recClass.getRecordComponents())
						.map(RecordComponent::getType)
						.toArray(Class<?>[]::new);
		try {
			return recClass.getDeclaredConstructor(paramTypes);
		} catch (Exception e) {
			throw new BinderException("failed to find the canonical constructor for the record %s".formatted(recClass.getName()), e);
		}
	}

	private static String computeNodeName(Field field) {
		final var xmlName = field.getAnnotation(XMLName.class);
		if (xmlName != null) {
			return xmlName.value();
		}
		return field.getName();
	}

	private static String computeNodeName(Class<?> dest, Annotation[] destAnnotations) {
		if (destAnnotations != null) {
			final var xmlArrElemNameAnn = Arrays
					.stream(destAnnotations)
					.filter(a -> a instanceof XMLArrayElementName)
					.findFirst();
			if (xmlArrElemNameAnn.isPresent()) {
				return ((XMLArrayElementName) xmlArrElemNameAnn.get()).value();
			}
		}
		return dest.getSimpleName().toLowerCase(Locale.ROOT);
	}

	private static String computeNodeName(RecordComponent comp) {
		final var xmlName = comp.getAnnotation(XMLName.class);
		if (xmlName != null) {
			return xmlName.value();
		}
		return comp.getName();
	}

	private static <T> void checkDocClass(Class<T> dest) throws BinderException {
		if (dest == null) {
			throw new BinderException("the document object cannot be null");
		}
		if (dest.isArray()) {
			throw new BinderException("the document object cannot be an array");
		}
		if (dest.isAnnotation()) {
			throw new BinderException("the document object cannot be an annotation");
		}
		if (dest.isEnum()) {
			throw new BinderException("the document object cannot be an enum");
		}
		if (dest.isInterface()) {
			throw new BinderException("the document object cannot be an interface");
		}
		if (dest.isPrimitive()) {
			throw new BinderException("the document object cannot be a primitive");
		}
		if (dest.isSynthetic()) {
			throw new BinderException("the document object cannot be a synthetic");
		}
	}

	private static <T> void checkDestClass(Class<T> dest) throws BinderException {
		if (dest == null) {
			throw new BinderException("the document class cannot be a null object");
		}
		if (dest.isAnnotation()) {
			throw new BinderException("the document class cannot be an annotation");
		}
		if (dest.isInterface()) {
			throw new BinderException("the document class cannot be an interface");
		}
	}

	private static String calculateNodeLocation(Node node) {
		if (node.getParentNode() == null) {
			return "/";
		}
		return calculateNodeLocation(node.getParentNode()) + "/" + node.getNodeName();
	}

	private void setUpDefaultParsers() {
		parsers.put(String.class, new StringParser());

		// Primitive types
		// For each primitive type there are two different Class objects.
		// For example: one for int.class and one for Integer.class.
		// This is the case in which the Java Language Specification and its API are contradictory.
		parsers.put(int.class, new IntParser());
		parsers.put(Integer.class, new IntParser());
		parsers.put(short.class, new ShortParser());
		parsers.put(Short.class, new ShortParser());
		parsers.put(long.class, new LongParser());
		parsers.put(Long.class, new LongParser());
		parsers.put(float.class, new FloatParser());
		parsers.put(Float.class, new FloatParser());
		parsers.put(double.class, new DoubleParser());
		parsers.put(Double.class, new DoubleParser());
		parsers.put(byte.class, new ByteParser());
		parsers.put(Byte.class, new ByteParser());
		parsers.put(char.class, new CharParser());
		parsers.put(Character.class, new CharParser());
		parsers.put(boolean.class, new BooleanParser());
		parsers.put(Boolean.class, new BooleanParser());

		parsers.put(URL.class, new URLParser());
		parsers.put(UUID.class, new UUIDParser());
	}

	/**
	 * Bind the XML document (<code>doc</code>) to a user-defined destination class (<code>destClass</code>).
	 * @param doc The XML document represented by a W3C DOM tree.
	 * @param destClass The user-defined class with possibly annotated fields that will contain the data from the document.
	 * @return An instance of the <code>destClass</code>.
	 * @param <T> The type of the destination class.
	 * @throws BinderException If binding fails due to missing nodes in the XML document or some other run-time error.
	 */
	public <T> T Bind(Document doc, Class<T> destClass) throws BinderException {
		checkDocClass(destClass);
		return parseFromNode(doc, destClass, null);
	}

	@SuppressWarnings("unchecked")
	private <T> T parseFromNode(Node srcNode, Class<T> destClass, Annotation[] destAnnotations) throws BinderException {
		checkDestClass(destClass);
		if (srcNode == null && Arrays.stream(destAnnotations).noneMatch(an -> an instanceof XMLOptional)) {
			throw new BinderException("failed to parse a non-optional field of a class %s: the source document node is null".formatted(destClass.getName()));
		}
		if (srcNode == null) {
			return null;
		}
		if (destClass.isArray()) {
			return parseIntoArray(srcNode, destClass, destAnnotations);
		}
		if (destClass.isRecord()) {
			return (T) parseIntoRecord(srcNode, (Class<? extends Record>) destClass, destAnnotations);
		}
		if (destClass.isEnum()) {
			return (T) parseIntoEnum(srcNode, (Class<? extends Enum<?>>) destClass, destAnnotations);
		}
		final var parser = parsers.get(destClass);
		if (parser != null) {
			final var parsed = parser.parseFromNode(srcNode, destAnnotations);
			if (parsed == null) {
				throw new BinderException("failed to parse %s from the node \"%s\": the parser returned a null value".formatted(destClass.getName(), calculateNodeLocation(srcNode)));
			}
			return (T) parsed;
		}
		return parseIntoClass(srcNode, destClass, destAnnotations);
	}

	@SuppressWarnings("unchecked")
	private <T extends Enum<T>> T parseIntoEnum(Node srcNode, Class<? extends Enum<?>> destClass, Annotation[] destAnnotations) throws BinderException {
		final var enumConstantName = srcNode.getTextContent().trim();
		if (destClass.isAnnotationPresent(XMLEnumUseString.class)) {
			for (final var constant : destClass.getEnumConstants()) {
				if (constant.toString().equals(enumConstantName)) {
					return (T) constant;
				}
			}
			throw new BinderException("failed to parse \"%s\" as an enum class instance %s: there's no enum constant whose string representation is \"%s\"".formatted(enumConstantName, destClass.getName(), enumConstantName));
		}
		try {
			final var valueOfMethod = destClass.getMethod("valueOf", String.class);
			return (T) valueOfMethod.invoke(null, enumConstantName);
		} catch (Exception e) {
			throw new BinderException("failed to parse \"%s\" as an enum class instance %s".formatted(enumConstantName, destClass.getName()), e);
		}
	}

	private <T> T parseIntoClass(Node srcNode, Class<T> destClass, Annotation[] destAnnotations) throws BinderException {
		try {
			final var parseCon = getClassParseConstructor(destClass);
			if (parseCon != null) {
				final var nodeText = Commons.extractString(srcNode);
				return parseCon.newInstance(nodeText);
			}
		} catch (Exception e) {
			throw new BinderException("failed to parse a node \"%s\" into a class %s using its class parse constructor".formatted(calculateNodeLocation(srcNode), destClass.getName()), e);
		}

		return parseClassFieldByField(srcNode, destClass, destAnnotations);
	}

	private <T> T parseClassFieldByField(Node srcNode, Class<T> destClass, Annotation[] destAnnotations) throws BinderException {
		if (srcNode instanceof Attr) {
			throw new BinderException("cannot parse a class %s field by field from an attribute \"%s\"".formatted(destClass.getName(), calculateNodeLocation(srcNode)));
		}
		final var instance = newClassInstance(destClass);
		for (final var field : destClass.getFields()) {
			final var elemName = computeNodeName(field);
			final var elemNode = getNode(srcNode, elemName, field.getDeclaredAnnotations());
			final var fieldValue = parseFromNode(elemNode, field.getType(), field.getDeclaredAnnotations());
			try {
				field.set(instance, fieldValue);
			} catch (IllegalAccessException e) {
				throw new BinderException("failed to assign the value \"%s\" to a field \"%s\" of a class %s".formatted(fieldValue, field.getName(), destClass.getName()), e);
			}
		}
		return instance;
	}

	private <T> Constructor<T> getClassParseConstructor(Class<T> destClass) throws BinderException {
		try {
			final var con = destClass.getConstructor(String.class);
			if (!con.isAnnotationPresent(XMLClassParseConstructor.class)) {
				return null;
			}
			if (!con.canAccess(null)) {
				throw new BinderException("cannot access the annotated class parse constructor of a class %s".formatted(destClass.getName()));
			}
			return con;
		} catch (NoSuchMethodException _) {
			return null;
		}
	}

	private <T extends Record> T parseIntoRecord(Node srcNode, Class<T> recClass, Annotation[] destAnnotations) throws BinderException {
		if (srcNode instanceof Attr) {
			throw new BinderException("cannot parse a record %s from an attribute \"%s\"".formatted(recClass.getName(), calculateNodeLocation(srcNode)));
		}
		final var recComps = recClass.getRecordComponents();
		final var recValues = new Object[recComps.length];
		for (int i = 0; i < recComps.length; i++) {
			final var recComp = recComps[i];
			final var nodeName = computeNodeName(recComp);
			final var recNode = getNode(srcNode, nodeName, recComp.getAnnotations());
			recValues[i] = parseFromNode(recNode, recComp.getType(), recComp.getAnnotations());
		}
		final var con = getRecordCanonicalConstructor(recClass);
		try {
			return con.newInstance(recValues);
		} catch (Exception e) {
			throw new BinderException("failed to create an instance of the record class %s".formatted(recClass.getName()), e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T parseIntoArray(Node srcNode, Class<T> arrClass, Annotation[] destAnnotations) throws BinderException {
		if (srcNode instanceof Attr) {
			throw new BinderException("cannot parse an array %s from an attribute \"%s\"".formatted(arrClass.getName(), calculateNodeLocation(srcNode)));
		}
		final var compType = arrClass.getComponentType();
		final var compNodeName = computeNodeName(compType, destAnnotations);
		final var compNodes = getNodes(srcNode, compNodeName);
		final var compObjects = new LinkedList<>();
		for (final var compNode : compNodes) {
			compObjects.addLast(parseFromNode(compNode, compType, null));
		}
		final var arr = Array.newInstance(compType, compObjects.size());
		for (int i = 0; i < compObjects.size(); i++) {
			Array.set(arr, i, compObjects.get(i));
		}
		return (T) arr;
	}

	private XPathNodes getNodes(Node contextNode, String elemName) throws BinderException {
		try {
			return xpath.evaluateExpression(elemName, contextNode, XPathNodes.class);
		} catch (XPathExpressionException e) {
			throw new BinderException("failed to find elements \"%s\" inside the node \"%s\"".formatted(elemName, calculateNodeLocation(contextNode)), e);
		}
	}

	private Node getNode(Node contextNode, String nodeName, Annotation[] destAnnotations) throws BinderException {
		if (Arrays.stream(destAnnotations).anyMatch(a -> a instanceof XMLFromAttribute)) {
			// Selects an attribute instead of an element
			nodeName = "@" + nodeName;
		}
		Node node;
		try {
			node = xpath.evaluateExpression(nodeName, contextNode, Node.class);
		} catch (XPathExpressionException e) {
			throw new BinderException("failed to find a element/attribute \"%s\" inside the node \"%s\"".formatted(nodeName, calculateNodeLocation(contextNode)), e);
		}
		return node;
	}

	private <T> T newClassInstance(Class<T> c) throws BinderException {
		try {
			final var con = getDefaultClassConstructor(c);
			return con.newInstance();
		} catch (Exception e) {
			throw new BinderException("failed to create an instance of the destination class %s".formatted(c.getName()), e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Constructor<T> getDefaultClassConstructor(Class<T> c) throws BinderException {
		final var cons = c.getDeclaredConstructors();
		boolean hasAccessibleCon = false;
		boolean hasEmptyCon = false;
		Constructor<?> selectedCon = null;
		for (final var con : cons) {
			if (con.getParameterCount() > 0) {
				continue;
			}
			hasEmptyCon = true;

			if (!con.canAccess(null)) {
				continue;
			}
			hasAccessibleCon = true;

			selectedCon = con;
		}
		if (!hasEmptyCon) {
			throw new BinderException("the class %s doesn't have an empty constructor".formatted(c.getName()));
		}
		if (!hasAccessibleCon) {
			throw new BinderException("the class %s doesn't have an accessible (public) empty constructor".formatted(c.getName()));
		}
		return (Constructor<T>) selectedCon;
	}
}
