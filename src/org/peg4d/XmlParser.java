package org.peg4d;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParser extends DefaultHandler {
	private boolean isFirstChild = false;
	private PrintStream stream;
	HashMap<String, HuffmanData> parseData = new HashMap<String, HuffmanData>();
	public XmlParser(HashMap<String, HuffmanData> parseData) {
		this.parseData = parseData;
	}

	public void setPrintStream(PrintStream fileoutStream) {
		this.stream = fileoutStream;
	}

	public void startDocument() {
		stream.println("==================<<< Start >>>======================");
		isFirstChild = true;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		stream.print("<");
		//stream.println(qName + " : " + this.parseData.get(qName).code);
		stream.println(qName + " ");
		int n = attributes.getLength();
		for(int i = 0; i < n; ++i){
			//stream.println(attributes.getLocalName(i) + " : " + this.parseData.get(attributes.getLocalName(i)).code);
			//stream.println(attributes.getValue(i) + " : " + this.parseData.get(attributes.getValue(i)).code);
			stream.print(attributes.getLocalName(i));
			stream.print("=");
			stream.print(attributes.getValue(i));
		}
		isFirstChild = true;
	}

	public void characters(char[] ch, int offset, int length) {
		String text = new String(ch, offset, length).trim();
		if(text.length() > 0){
			stream.println(isFirstChild ? "" : ",");
			stream.print("{ \"name\": \"@text\", \"value\": \"");
			stream.print(text);
			stream.print("\" }");
			isFirstChild = false;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		stream.print("] }");
		isFirstChild = false;
	}

	public void endDocument() {
		stream.println("] }");
		isFirstChild = false;
	}

}
