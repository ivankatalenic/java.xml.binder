# java.xml.binder: A library for binding XML to user-defined classes

A Java library for binding XML documents to user-defined Java classes.
It defines annotations that guide the binding process.

## Canonical example

Suppose we have an XML document with the following schema:

```xml
<?xml version="1.0" encoding="utf-8"?>
<statement>
	<account owner="Google" balance="10000.0"/>
</statement>
```

We would then need the following declarations to bind to the XML document:

```java
public record XmlDto(Statement statement) {};
public record Statement(Account account) {};
public record Account(@XMLFromAttribute String owner, @XMLFromAttribute Double balance) {};
```

Check out the complete canonical example in [BinderTests.java](src/test/java/com/ivankatalenic/java/xml/binder/test/BinderTests.java) file.

## Features

- Binding XML data into classes, records, enums, and arrays.
- Able to extract data from attributes in addition to extracting from XML elements. Use [`@XMLFromAttribute`](src/main/java/com/ivankatalenic/java/xml/binder/annotations/XMLFromAttribute.java) annotation.
- Able to parse XML data with custom class parse constructors. Use [`@XMLClassParseConstructor`](src/main/java/com/ivankatalenic/java/xml/binder/annotations/XMLClassParseConstructor.java) annotation. 
- Able to mark a field as optional, so that a missing XML element/attribute won't cause errors. Use [`@XMLOptional`](src/main/java/com/ivankatalenic/java/xml/binder/annotations/XMLOptional.java) annotation.
- Able to parse a field from a different name. Use [`@XMLName`](src/main/java/com/ivankatalenic/java/xml/binder/annotations/XMLName.java) annotation.

## FAQ

### Why another XML binding library?

It's mainly about the learning experience.
I'm developing another project without any third-party libraries and this was a necessary component of it.
I thought it'd be useful to extract it to a standalone library so others can use it.

### How optimized is this library?

This library wasn't benchmarked or profiled to improve any performance-critical parsing paths.

### Can I contribute?

Yes. Feel free to create a PR with your improvements. However, please describe what you are improving and why, in details.

### Is this library free of bugs?

No. Please report any bugs you encountered, along with replication steps.
