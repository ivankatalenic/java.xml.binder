package com.ivankatalenic.java.xml.binder.test;

import com.ivankatalenic.java.xml.binder.Binder;
import com.ivankatalenic.java.xml.binder.BinderException;
import com.ivankatalenic.java.xml.binder.annotations.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BinderTests {
	private DocumentBuilder domParser;
	private Binder binder;

	@BeforeEach
	public void initParser() throws ParserConfigurationException {
		domParser = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
		binder = new Binder();
	}

	public record XmlDto(Statement statement) {};
	public record Statement(Account account) {};
	public record Account(@XMLFromAttribute String owner, @XMLFromAttribute Double balance) {};
	@Test
	public void canonicalExample() throws IOException, BinderException, SAXException {
		final var xmlDoc = """
				<?xml version="1.0" encoding="utf-8"?>
				<statement>
					<account owner="Google" balance="10000.0"/>
				</statement>
				""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var dto = binder.bind(dom, XmlDto.class);

		assertEquals("Google", dto.statement().account().owner());
		assertEquals(10000.0, dto.statement().account().balance());
	}

	public static class SingleStringDoc {
		public String root;
	}
	@Test
	public void singleString() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<root>Hello!</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, SingleStringDoc.class);

		assertEquals("Hello!", doc.root);
	}

	public static class ContainerDoc {
		public static class NestedDoc {
			public int num;
		}
		public NestedDoc root;
	}
	@Test
	public void nestedNumber() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<root>
							<num>25</num>
						</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, ContainerDoc.class);

		assertEquals(25, doc.root.num);
	}

	public static class EnumDoc {
		public enum ThreeEnum {
			E1, E2, E3;
		}
		public ThreeEnum root;
	}
	@Test
	public void enumConstant() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<root>E2</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, EnumDoc.class);

		assertEquals(EnumDoc.ThreeEnum.E2, doc.root);
	}

	public static class StringEnumDoc {
		@XMLEnumUseString
		public enum StringEnum {
			E1("e1"), E2("e2"), E3("e3");

			private final String val;
			StringEnum(String val) { this.val = val; }
			@Override
			public String toString() {
				return val;
			}
		}
		public StringEnum root;
	}
	@Test
	public void enumConstantFromString() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<root>e2</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, StringEnumDoc.class);

		assertEquals(StringEnumDoc.StringEnum.E2, doc.root);
	}

	public static class UrlDoc {
		public URL root;
	}
	@Test
	public void url() throws IOException, SAXException, BinderException, URISyntaxException {
		final var xmlDoc = """
						<root>https://ivankatalenic.com</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, UrlDoc.class);

		assertEquals(new URI("https://ivankatalenic.com").toURL(), doc.root);
	}

	public static class UuidDoc {
		public UUID root;
	}
	@Test
	public void uuid() throws Exception {
		final var xmlDoc = """
						<root>239e6b5e-78f5-4c7d-bf6d-adee98bc8b8f</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, UuidDoc.class);

		assertEquals(UUID.fromString("239e6b5e-78f5-4c7d-bf6d-adee98bc8b8f"), doc.root);
	}

	public static class ClassParseDoc {
		public static class A {
			public final String str1;
			public final String str2;
			@XMLClassParseConstructor
			public A(String raw) {
				final var strs = raw.split(",", 2);
				str1 = strs[0];
				str2 = strs[1];
			}
		}
		public A root;
	}
	@Test
	public void classParseConstructor() throws Exception {
		final var xmlDoc = """
						<root>Hello,World</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, ClassParseDoc.class);

		assertEquals("Hello", doc.root.str1);
		assertEquals("World", doc.root.str2);
	}

	public static class OptionalElemDoc {
		public static class A {
			public String str;
			@XMLOptional
			public String opt;
		}
		public A root;
	}
	@Test
	public void optionalElementMissing() throws Exception {
		final var xmlDoc = """
						<root>
							<str>Hello</str>
						</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, OptionalElemDoc.class);

		assertEquals("Hello", doc.root.str);
		assertNull(doc.root.opt);
	}

	@Test
	public void optionalElementPresent() throws Exception {
		final var xmlDoc = """
						<root>
							<str>Hello</str>
							<opt>World</opt>
						</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, OptionalElemDoc.class);

		assertEquals("Hello", doc.root.str);
		assertEquals("World", doc.root.opt);
	}

	public static class MissingElemDoc {
		public static class A {
			public String str1;
			public String str2;
		}
		public A root;
	}
	@Test
	public void nonOptionalElementMissing() throws Exception {
		final var xmlDoc = """
						<root>
							<str2>World</str2>
						</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		assertThrows(BinderException.class, () -> binder.bind(dom, MissingElemDoc.class));
	}

	public static class AttributeDoc {
		public static class A {
			@XMLFromAttribute
			public String str1;
			public String str2;
		}
		public A root;
	}
	@Test
	public void fromAttribute() throws Exception {
		final var xmlDoc = """
						<root str1="Hello" str2="There">
							<str1>Hi</str1>
							<str2>World</str2>
						</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, AttributeDoc.class);
		assertEquals("Hello", doc.root.str1);
		assertEquals("World", doc.root.str2);
	}

	public static class AttributeNameDoc {
		public static class A {
			@XMLFromAttribute
			@XMLName("customName")
			public String str1;
			public String str2;
		}
		public A root;
	}
	@Test
	public void fromAttributeWithDifferentName() throws Exception {
		final var xmlDoc = """
						<root str1="Howdy" str2="There" customName="Hello">
							<str1>Hi</str1>
							<str2>World</str2>
						</root>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, AttributeNameDoc.class);
		assertEquals("Hello", doc.root.str1);
		assertEquals("World", doc.root.str2);
	}
}
