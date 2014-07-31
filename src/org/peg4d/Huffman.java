package org.peg4d;

import java.io.File;
import java.io.PrintStream;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Huffman {
	String fileName;
	HashMap<String, HashMap<String, HuffmanData>> data;
	HashMap<String, HuffmanData> parseData = new HashMap<String, HuffmanData>();
	ArrayList<HuffmanData> huffmandatalist = new ArrayList<HuffmanData>();
	ArrayList<String> keyword = new ArrayList<String>();
	int powsize;
	
	public void initXmlMap() {
		HashMap<String, HuffmanData> hmd = new HashMap<String, HuffmanData>();
		for(int i = 0; i < keyword.size(); i++) {
			HuffmanData d = new HuffmanData("#default", keyword.get(i), Integer.MAX_VALUE);
			hmd.put(keyword.get(i), d);
		}
		data.put("#default", hmd);
	}
	
	public Huffman(String fileName) {
		this.fileName = fileName;
		data = new HashMap<String, HashMap<String, HuffmanData>>();
		keyword.add("<");
		keyword.add(">");
		keyword.add("/>");
		keyword.add("=");
		this.initXmlMap();
	}
	
	public Pego traverseNode(Pego pego) {
		if(pego.size() == 0) {
			String key = pego.tag;
			String value = pego.getText();
			if(!this.data.containsKey(key)) {
				HashMap<String, HuffmanData> hm = new HashMap<String, HuffmanData>();
				HuffmanData hd = new HuffmanData(key, value);
				hm.put(value, hd);
				data.put(key, hm);
			}
			else {
				HashMap<String, HuffmanData> hm = this.data.get(key);
				if(hm.containsKey(value)) {
					hm.get(value).counter++;
				}
				else {
					HuffmanData hd = new HuffmanData(key, value);
					hm.put(value, hd);
					data.put(key, hm);
				}
			}
			System.out.println("This node is end node: " + "tag: " + pego.tag + ", test: " + pego.getText());
			return null;
		}
		for(int i = 0; i < pego.size(); i++) {
			traverseNode(pego.AST[i]);
		}
		return null;
	}

	public void setList() {
		for(String key : this.data.keySet()) {
			HashMap<String, HuffmanData> hm = this.data.get(key);
			for(String _key : hm.keySet()) {
				System.out.println("_key    = " + _key);
				System.out.println("counter = " + hm.get(_key).counter);
				this.huffmandatalist.add(hm.get(_key));
			}
		}
	}

	public void setBit() {
		System.out.println(this.huffmandatalist.size());
		int i = 0;
		int listsize = this.huffmandatalist.size();
		int buffer;
		while(true) {
			buffer = listsize >> i;
			if(buffer == 0) {
				break;
			}
			i++;
		}
		this.powsize = i;
	}
	
	public void sortList() {
		System.out.println("-----------------<<< sortList >>-----------------------");
		Collections.sort(this.huffmandatalist, new HuffmanData(null, null));
		for(int i = 0; i < this.huffmandatalist.size(); i++) {
			HuffmanData d = this.huffmandatalist.get(i);
			System.out.println("tag: " + d.tag);
			System.out.println("source: " + d.source);
			System.out.println("counter: " + d.counter);
		}
	}
//	HashMap<String, HashMap<String, HuffmanData>> data;
//	ArrayList<HuffmanData> huffmandatalist = new ArrayList<HuffmanData>();
//	ArrayList<String> keyword = new ArrayList<String>();
//	int powsize;
	
	public void coding() {
		System.out.println("----------------<<< coding >>>----------------------");
		int size = huffmandatalist.size();
		huffmandatalist.get(0).code += "0";
		for(int i = 1; i < size; i++) {
			for(int j = 0; j < i; j++) {
				huffmandatalist.get(i).code += "1";
			}
			huffmandatalist.get(i).code += "0";
		}
		for(int i = 0; i < this.huffmandatalist.size(); i++) {
			HuffmanData d = this.huffmandatalist.get(i);
			System.out.println("tag: " + d.tag);
			System.out.println("source: " + d.source);
			System.out.println("counter: " + d.counter);
			System.out.println("code: " + d.code);
		}
	}
	
	public void xmlparser() {
		try {
			XmlParser converter = new XmlParser(this.parseData);
			PrintStream fileoutStream = null;
			//fileoutStream = new PrintStream("/output.xml");
			converter.setPrintStream(fileoutStream);
			converter.setPrintStream(System.out);
			SAXParserFactory spfactory = SAXParserFactory.newInstance();
			SAXParser parser = spfactory.newSAXParser();
			parser.parse(new File(this.fileName), converter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void rehash() {
		for(String key : this.data.keySet()) {
			HashMap<String, HuffmanData> hm = this.data.get(key);
			for(String _key : hm.keySet()) {
				this.parseData.put(_key, hm.get(_key));
			}
		}
	}
	
	public void encode(Pego pego) {
		this.traverseNode(pego);
		System.out.println(this.data);
		this.setList();
		this.setBit();
		this.sortList();
		this.coding();
		this.rehash();
		this.xmlparser();
		System.out.println(Math.pow(2, this.powsize));
		System.out.println("=======================================");
	}
}
