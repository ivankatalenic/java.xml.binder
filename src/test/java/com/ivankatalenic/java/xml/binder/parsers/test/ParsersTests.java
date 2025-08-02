package com.ivankatalenic.java.xml.binder.parsers.test;

import com.ivankatalenic.java.xml.binder.Binder;
import com.ivankatalenic.java.xml.binder.BinderException;
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

public class ParsersTests {
	private DocumentBuilder domParser;
	private Binder binder;

	@BeforeEach
	public void initParser() throws ParserConfigurationException {
		domParser = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
		binder = new Binder();
	}

	public static class StringDoc {
		public String elem;
	}
	@Test
	public void stringTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>Hello!</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, StringDoc.class);

		assertEquals("Hello!", doc.elem);
	}
	@Test
	public void stringEmptyTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem></elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, StringDoc.class);

		assertEquals("", doc.elem);
	}
	@Test
	public void stringSpaceOnlyTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>\t </elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, StringDoc.class);

		assertEquals("\t ", doc.elem);
	}

	public static class BooleanDoc {
		public Boolean elem;
	}
	@Test
	public void booleanTrueTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>true</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, BooleanDoc.class);

		assertEquals(true, doc.elem);
	}
	@Test
	public void booleanFalseTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>false</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, BooleanDoc.class);

		assertEquals(false, doc.elem);
	}
	@Test
	public void booleanCapitalTrueTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>True</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, BooleanDoc.class);

		assertEquals(true, doc.elem);
	}
	@Test
	public void booleanCapitalFalseTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>False</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, BooleanDoc.class);

		assertEquals(false, doc.elem);
	}

	public static class ByteDoc {
		public Byte elem;
	}
	@Test
	public void byteDecimalTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>127</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, ByteDoc.class);

		assertEquals((byte) 127, doc.elem);
	}
	@Test
	public void byteNegativeDecimalTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>-1</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, ByteDoc.class);

		assertEquals((byte) -1, doc.elem);
	}
	@Test
	public void byteHexTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>0x5F</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, ByteDoc.class);

		assertEquals((byte) 0x5F, doc.elem);
	}
	@Test
	public void byteHexBigXTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>0X5f</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, ByteDoc.class);

		assertEquals((byte) 0x5F, doc.elem);
	}
	@Test
	public void byteHexHashTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>#5f</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, ByteDoc.class);

		assertEquals((byte) 0x5F, doc.elem);
	}
	@Test
	public void byteOctalTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>0137</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, ByteDoc.class);

		assertEquals((byte) 0x5F, doc.elem);
	}

	public static class CharDoc {
		public Character elem;
	}
	@Test
	public void charTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>a</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, CharDoc.class);

		assertEquals('a', doc.elem);
	}
	@Test
	public void charTabTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>\t</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, CharDoc.class);

		assertEquals('\t', doc.elem);
	}
	@Test
	public void charMultipleTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>hello world</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, CharDoc.class);

		assertEquals('h', doc.elem);
	}

	public static class DoubleDoc {
		public Double elem;
	}
	@Test
	public void doubleTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>127</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, DoubleDoc.class);

		assertEquals(127.0, doc.elem);
	}
	@Test
	public void doubleNegTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>-127.25</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, DoubleDoc.class);

		assertEquals(-127.25, doc.elem);
	}

	public static class FloatDoc {
		public Float elem;
	}
	@Test
	public void floatTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>127</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, FloatDoc.class);

		assertEquals(127.0f, doc.elem);
	}
	@Test
	public void floatNegTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>-127.25</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, FloatDoc.class);

		assertEquals(-127.25f, doc.elem);
	}

	public static class IntDoc {
		public Integer elem;
	}
	@Test
	public void intTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>127</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, IntDoc.class);

		assertEquals(127, doc.elem);
	}
	@Test
	public void intNegTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>-127</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, IntDoc.class);

		assertEquals(-127, doc.elem);
	}

	public static class LongDoc {
		public Long elem;
	}
	@Test
	public void longTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>127</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, LongDoc.class);

		assertEquals(127L, doc.elem);
	}
	@Test
	public void longNegTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>-127</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, LongDoc.class);

		assertEquals(-127L, doc.elem);
	}

	public static class ShortDoc {
		public Short elem;
	}
	@Test
	public void shortTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>127</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, ShortDoc.class);

		assertEquals((short) 127, doc.elem);
	}
	@Test
	public void shortNegTest() throws IOException, SAXException, BinderException {
		final var xmlDoc = """
						<elem>-127</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, ShortDoc.class);

		assertEquals((short) -127, doc.elem);
	}

	public static class UrlDoc {
		public URL elem;
	}
	@Test
	public void urlTest() throws IOException, SAXException, BinderException, URISyntaxException {
		final var xmlDoc = """
						<elem>https://ivankatalenic.com/?hello=world#frag</elem>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, UrlDoc.class);

		assertEquals(new URI("https://ivankatalenic.com/?hello=world#frag").toURL(), doc.elem);
	}

	public static class UuidDoc {
		public UUID elem;
	}
	@Test
	public void uuidTest() throws IOException, SAXException, BinderException, URISyntaxException {
		final var exampleUuid = UUID.randomUUID();
		final var xmlDoc = """
						<elem>%s</elem>
						""".formatted(exampleUuid);
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, UuidDoc.class);

		assertEquals(exampleUuid, doc.elem);
	}


	public static class PrimitiveDoc {
		public static class Primitives {
			public boolean b;
			public byte by;
			public char c;
			public double d;
			public float f;
			public int i;
			public long l;
			public short s;
		}
		public Primitives primitives;
	}
	@Test
	public void primitiveTest() throws IOException, SAXException, BinderException, URISyntaxException {
		final var xmlDoc = """
						<primitives>
							<b>true</b>
							<by>27</by>
							<c>i</c>
							<d>3.14</d>
							<f>1.602</f>
							<i>1337</i>
							<l>300000</l>
							<s>1013</s>
						</primitives>
						""";
		final var dom = domParser.parse(new InputSource(new StringReader(xmlDoc)));

		final var doc = binder.bind(dom, PrimitiveDoc.class);

		assertTrue(doc.primitives.b);
		assertEquals((byte) 27, doc.primitives.by);
		assertEquals('i', doc.primitives.c);
		assertEquals(3.14, doc.primitives.d);
		assertEquals(1.602f, doc.primitives.f);
		assertEquals(1337, doc.primitives.i);
		assertEquals(300_000, doc.primitives.l);
		assertEquals(1013, doc.primitives.s);
	}
}
