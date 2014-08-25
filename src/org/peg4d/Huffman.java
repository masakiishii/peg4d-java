package org.peg4d;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Huffman {
	HashMap<String, HuffmanData> huffmanData     = new HashMap<String, HuffmanData>();
	HashMap<String, String>      encodeMap       = new HashMap<String, String>();
	HashMap<String, String>      decodeMap       = new HashMap<String, String>();
	ArrayList<String>            xmlKeyword      = new ArrayList<String>();
	ArrayList<HuffmanData>       huffmanDataList = new ArrayList<HuffmanData>();
	ArrayList<Boolean>           encodeSource    = new ArrayList<Boolean>();
	int                          codeSize;
	
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
		System.out.println(pego.source);
		System.out.println(pego.source.getClass());
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
			System.out.println("this node is : " + pego.tag);
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

	public void showCode(ArrayList<Boolean> code) {
		for(int i = 0; i < code.size(); i++) {
			if(code.get(i)) {
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
	public void showList (ArrayList<HuffmanData> list) {
		for(int i = 0; i < list.size(); i++) {
			System.out.println("======================================================");
			System.out.println("tag: " + list.get(i).tag);
			System.out.println("term: "  + list.get(i).term);
			System.out.println("occurence: "  + list.get(i).occurence);
			System.out.print("code: ");
			showCode(list.get(i).code);
			System.out.println();
		}
	}

	private void setList() {
		for(String key : this.huffmanData.keySet()) {
			this.huffmanDataList.add(this.huffmanData.get(key));
		}
		this.codeSize = this.huffmanDataList.size() - 1;
	}

	private void sortList() {
		Collections.sort(this.huffmanDataList, new HuffmanData(null, null));
	}

	private void coding() {
		int size = this.huffmanDataList.size();
		this.huffmanDataList.get(0).code.add(false);
		for(int i = 1; i < size; i++) {
			System.out.println(this.huffmanDataList.get(i).term);
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
		case '>': case '=':case '"':
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
			case ' ': case '\n': case '\t': case '=' :
				this.encodeSource.addAll(this.huffmanData.get(String.valueOf(c)).code);
				break;
			case '<':
				if(s.charAt(i+1) == '/') {
					this.encodeSource.addAll(this.huffmanData.get("</").code);
					i++;
				}
				else if(s.charAt(i+1) == '!') {
					this.encodeSource.addAll(this.huffmanData.get("<!--").code);
					i = i + 3;
					//while(s.charAt(i) == '-') i++;
					for(;s.charAt(i) == '-' && s.charAt(i+1) == '-' && s.charAt(i+2) == '>'; i++){}
				}
				else {
					this.encodeSource.addAll(this.huffmanData.get("<").code);
				}
				break;
			case '>':
				if(s.charAt(i-1) == '/') {
					this.encodeSource.addAll(this.huffmanData.get("/>").code);
				}
				else if(s.charAt(i-1) == '-' && s.charAt(i-2) == '-') {
					this.encodeSource.addAll(this.huffmanData.get("-->").code);
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
			case '"':
				this.encodeSource.addAll(this.huffmanData.get(String.valueOf(c)).code);
				StringBuffer name = new StringBuffer();
				while(s.charAt(++i) != '"') {
					name.append(s.charAt(i));
				}
				System.out.println(name.toString());
				if(this.huffmanData.containsKey(name.toString())) {
					this.encodeSource.addAll(this.huffmanData.get(name.toString()).code);
				}
				this.encodeSource.addAll(this.huffmanData.get(String.valueOf(c)).code);
				break;
			default:
				StringBuffer sb = new StringBuffer();
				int pos = i;
				while(!isKeyword(s.charAt(pos))) {
					sb.append(s.charAt(pos));
					pos++;
				}
				if(i != pos) i = pos - 1;
				System.out.println(sb.toString());
				if(this.huffmanData.containsKey(sb.toString())) {
					this.encodeSource.addAll(this.huffmanData.get(sb.toString()).code);
				}
				break;
			}
		}
	}

	private void converter(ArrayList<Boolean> decodeData, int source) {
		int counter = 0;
		int flag;
		while(counter != 8) {
			flag = source & (1 << (7 - counter++));
			if(flag > 0) {
				decodeData.add(true);
			}
			else {
				decodeData.add(false);
			}
		}
	}
	
	private void outputBinaryData(String file) {
		try {
			BitOutputStream out = new BitOutputStream(new FileOutputStream(file));
			out.write(this.encodeSource);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readBinaryData(String file) {
		try {
			BitInputStream in = new BitInputStream(new FileInputStream(file));
			ArrayList<Boolean> decodeData = new ArrayList<Boolean>();
			int c;
			while((c = in.read()) != -1) {
				converter(decodeData, c);
			}
			parseBinaryData(decodeData);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parseBinaryData(ArrayList<Boolean> decodeData) {
		try {
			showCode(decodeData);
			System.out.println("");
			StringBuffer buf = new StringBuffer();
			File file = new File("decodedbuild.xml");
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			
			for(int i = 0; i < decodeData.size(); i++) {
				if(decodeData.get(i)) {
					buf.append("1");
					if(buf.length() == this.codeSize) {
						System.out.print(this.decodeMap.get(buf.toString()));
						pw.print(this.decodeMap.get(buf.toString()));
						buf.delete(0, buf.length());
					}
				}
				else {
					buf.append("0");
					System.out.print(this.decodeMap.get(buf.toString()));
					pw.print(this.decodeMap.get(buf.toString()));
					buf.delete(0, buf.length());
				}
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String convertBooleanArrayListToString(ArrayList<Boolean> code) {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < code.size(); i++) {
			if(code.get(i)) {
				buf.append("1");
			}
			else {
				buf.append("0");
			}
		}
		return buf.toString();
	}
	
	private void buildMap() {
		for(int i = 0; i < this.huffmanDataList.size(); i++) {
			String key   = convertBooleanArrayListToString(this.huffmanDataList.get(i).code);
			String value = this.huffmanDataList.get(i).term;
			this.decodeMap.put(key, value);
			this.encodeMap.put(value, key);
		}
	}
	
	public ArrayList<HuffmanData> getHuffmanDataList(String tag) {
		ArrayList<HuffmanData> datalist = new ArrayList<HuffmanData>();
		for(int i = 0; i < this.huffmanDataList.size(); i++) {
			HuffmanData hd = this.huffmanDataList.get(i);
			if(hd.tag.equals(tag)) {
				datalist.add(hd);
			}
		}
		return datalist;
	}
	
	public void encode(Pego pego) {
		parse(pego);
		traverseNode(pego);
		setList();
		sortList();
		coding();
		showList(this.huffmanDataList);
		output(pego);
		showCode(this.encodeSource);
		buildMap();
		outputBinaryData("output.txt");
		System.out.println("=======================================");
		readBinaryData("output.txt");
		System.out.println("==================<<< Recompile >>>=====================");
		
		Recompiler recompiler = new Recompiler(this, "sample/xml.peg", "recompiled.peg");
		recompiler.generatePegFile();
		System.out.println("=======================================");
	}
}
