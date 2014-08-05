package org.peg4d;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Huffman {
	HashMap<String, HuffmanData> huffmanData = new HashMap<String, HuffmanData>();
	ArrayList<String> xmlKeyword = new ArrayList<String>();
	ArrayList<HuffmanData> huffmanDataList = new ArrayList<HuffmanData>();
	ArrayList<Boolean> encodeSource = new ArrayList<Boolean>();
	
	public void initXmlMap() {
		for(int i = 0; i < xmlKeyword.size(); i++) {
			HuffmanData d = new HuffmanData("#default", xmlKeyword.get(i));
			huffmanData.put(xmlKeyword.get(i), d);
		}
	}
	
	public Huffman() {
		xmlKeyword.add(" ");
		xmlKeyword.add("\n");
		xmlKeyword.add("\t");
		xmlKeyword.add("<");
		xmlKeyword.add("</");
		xmlKeyword.add("<!--");
		xmlKeyword.add(">");
		xmlKeyword.add("/>");
		xmlKeyword.add("-->");
		xmlKeyword.add("=");
		xmlKeyword.add("\"");
		this.initXmlMap();
	}
	
	private void parse(Pego pego) {
		String s = ((StringSource)(pego.source)).sourceText;
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch(c) {
			case ' ': case '\n': case '\t': case '=': case '"':
				this.huffmanData.get(String.valueOf(c)).occurence++;
				break;
			case '<':
				if(s.charAt(i+1) == '/') {
					this.huffmanData.get("</").occurence++;
					i++;
				}
				else if(s.charAt(i+1) == '!') {
					this.huffmanData.get("<!--").occurence++;
					i = i + 2;
					while(s.charAt(i) == '-') i++;
				}
				else {
					this.huffmanData.get("<").occurence++;
				}
				break;
			case '>':
				if(s.charAt(i-1) == '/') {
					this.huffmanData.get("/>").occurence++;
				}
				else {
					this.huffmanData.get(">").occurence++;
				}
				break;
			case '-':
				if(s.charAt(i+1) == '-' && s.charAt(i+2) == '>') {
					this.huffmanData.get("-->").occurence++;
					i = i + 2;
				}
				else {
					continue;
				}
				break;
			default:
				break;
			}
		}
	}

	private void traverseNode(Pego pego) {
		if(pego.size() == 0) {
			String key = pego.getText();
			if(!this.huffmanData.containsKey(key)) {
				HuffmanData value = new HuffmanData(pego.tag, pego.getText());
				value.occurence++;
				this.huffmanData.put(key, value);
			}
			else {
				this.huffmanData.get(key).occurence++;
			}
		}
		for(int i = 0; i < pego.size(); i++) {
			traverseNode(pego.AST[i]);
		}
	}

	private void showCode(ArrayList<Boolean> code) {
		for(int i = 0; i < code.size(); i++) {
			if(code.get(i).equals(true)) {
				System.out.print("1");
			}
			else {
				System.out.print("0");
			}
		}
	}
	
	private void showMap () {
		for(String key : this.huffmanData.keySet()) {
			System.out.println("======================================================");
			System.out.println("key: " + key);
			System.out.println("tag: " + this.huffmanData.get(key).tag);
			System.out.println("term: "  + this.huffmanData.get(key).term);
			System.out.println("occurence: "  + this.huffmanData.get(key).occurence);
			System.out.print("code: ");
			showCode(this.huffmanData.get(key).code);
			System.out.println();
		}
	}
	private void showList () {
		for(int i = 0; i < this.huffmanDataList.size(); i++) {
			System.out.println("======================================================");
			System.out.println("tag: " + this.huffmanDataList.get(i).tag);
			System.out.println("term: "  + this.huffmanDataList.get(i).term);
			System.out.println("occurence: "  + this.huffmanDataList.get(i).occurence);
			System.out.print("code: ");
			showCode(this.huffmanDataList.get(i).code);
			System.out.println();
		}
	}

	private void setList() {
		for(String key : this.huffmanData.keySet()) {
			this.huffmanDataList.add(this.huffmanData.get(key));
		}
	}

	private void sortList() {
		Collections.sort(this.huffmanDataList, new HuffmanData(null, null));
	}

	private void coding() {
		int size = this.huffmanDataList.size();
		this.huffmanDataList.get(0).code.add(false);
		for(int i = 1; i < size; i++) {
			for(int j = 0; j < i; j++) {
				this.huffmanDataList.get(i).code.add(true);
			}
			if(i != size-1) {
				this.huffmanDataList.get(i).code.add(false);
			}
			else {
				continue;
			}
		}
	}

	private boolean isKeyword(char literal) {
		switch(literal) {
		case ' ': case '\n': case '\t': case '<':
		case '>': case '/':  case '-':  case '=':case '"':
			return true;
		default:
			return false;
		}
	}
	
	private void output(Pego pego) {
		String s = ((StringSource)(pego.source)).sourceText;
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch(c) {
			case ' ': case '\n': case '\t': case '=': case '"':
				this.encodeSource.addAll(this.huffmanData.get(String.valueOf(c)).code);
				break;
			case '<':
				if(s.charAt(i+1) == '/') {
					this.encodeSource.addAll(this.huffmanData.get("</").code);
					i++;
				}
				else if(s.charAt(i+1) == '!') {
					this.encodeSource.addAll(this.huffmanData.get("<!--").code);
					i = i + 2;
					while(s.charAt(i) == '-') i++;
				}
				else {
					this.encodeSource.addAll(this.huffmanData.get("<").code);
				}
				break;
			case '>':
				if(s.charAt(i-1) == '/') {
					this.encodeSource.addAll(this.huffmanData.get("/>").code);
				}
				else {
					this.encodeSource.addAll(this.huffmanData.get(">").code);
				}
				break;
			case '-':
				if(s.charAt(i+1) == '-' && s.charAt(i+2) == '>') {
					this.encodeSource.addAll(this.huffmanData.get("-->").code);
					i = i + 2;
				}
				else {
					continue;
				}
				break;
			default:
				StringBuffer sb = new StringBuffer();
				while(!isKeyword(s.charAt(i))) {
					sb.append(s.charAt(i));
					i++;
				}
				if(this.huffmanData.containsKey(sb.toString())) {
					this.encodeSource.addAll(this.huffmanData.get(sb.toString()).code);
				}
				break;
			}
		}
	}
	
	public void encode(Pego pego) {
		parse(pego);
		traverseNode(pego);
		setList();
		sortList();
		coding();
		//showList();
		output(pego);
		System.out.println("=======================================");
	}
}
