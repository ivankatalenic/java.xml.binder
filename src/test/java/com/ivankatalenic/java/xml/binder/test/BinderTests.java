package com.ivankatalenic.java.xml.binder.test;

import com.ivankatalenic.java.xml.binder.Binder;
import com.ivankatalenic.java.xml.binder.BinderException;
import com.ivankatalenic.binder.annotations.*;
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

	public static class document1 {
		public String root;
	}
	@Test
	public void singleString() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<root>Hello!</root>
						""";
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document1.class);

		assertEquals("Hello!", doc.root);
	}

	public static class document2 {
		public static class doc_in {
			public int num;
		}
		public doc_in root;
	}
	@Test
	public void nestedNumber() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<root>
							<num>25</num>
						</root>
						""";
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document2.class);

		assertEquals(25, doc.root.num);
	}

	public static class document4 {
		public enum e {
			e1, e2, e3;
		}
		public e root;
	}
	@Test
	public void enumConstant() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<root>e2</root>
						""";
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document4.class);

		assertEquals(document4.e.e2, doc.root);
	}

	public static class document5 {
		@XMLEnumUseString
		public enum e {
			E1("e1"), E2("e2"), E3("e3");

			private final String val;
			e(String val) { this.val = val; }
			@Override
			public String toString() {
				return val;
			}
		}
		public e root;
	}
	@Test
	public void enumConstantFromString() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<root>e2</root>
						""";
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document5.class);

		assertEquals(document5.e.E2, doc.root);
	}

	public static class document6 {
		public URL root;
	}
	@Test
	public void url() throws IOException, SAXException, BinderException, URISyntaxException {
		final var xmlDoc = """
						<root>https://ivankatalenic.com</root>
						""";
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document6.class);

		assertEquals(new URI("https://ivankatalenic.com").toURL(), doc.root);
	}

	public static class document7 {
		public UUID root;
	}
	@Test
	public void uuid() throws Exception {
		final var xmlDoc = """
						<root>239e6b5e-78f5-4c7d-bf6d-adee98bc8b8f</root>
						""";
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document7.class);

		assertEquals(UUID.fromString("239e6b5e-78f5-4c7d-bf6d-adee98bc8b8f"), doc.root);
	}

	public static class document8 {
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
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document8.class);

		assertEquals("Hello", doc.root.str1);
		assertEquals("World", doc.root.str2);
	}

	public static class document9 {
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
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document9.class);

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
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document9.class);

		assertEquals("Hello", doc.root.str);
		assertEquals("World", doc.root.opt);
	}

	public static class document10 {
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
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		assertThrows(BinderException.class, () -> binder.Bind(dom, document10.class));
	}

	public static class document11 {
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
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document11.class);
		assertEquals("Hello", doc.root.str1);
		assertEquals("World", doc.root.str2);
	}

	public static class document12 {
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
		final var dom = domParser.parse(new InputSource(StringReader.of(xmlDoc)));

		final var doc = binder.Bind(dom, document12.class);
		assertEquals("Hello", doc.root.str1);
		assertEquals("World", doc.root.str2);
	}
}
