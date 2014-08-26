package org.peg4d;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recompiler {
	private Huffman huffmanInfo;
	private String  input;
	private String  output;
	
	public Recompiler(Huffman huffmanInfo, String input, String output) {
		this.huffmanInfo = huffmanInfo;
		this.input       = input;
		this.output      = output;
	}
	

	
	private void recompile() {
		try {
			File inputfile = new File(input);
			BufferedReader bufferedreader = new BufferedReader(new FileReader(inputfile));
			String  line;
			String  regex   = "#[a-zA-Z]*";
			Pattern pattern = Pattern.compile(regex);
			while((line = bufferedreader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if(matcher.find()) {
					putDataMap(matcher.group(), line);
				}
				else {
					System.out.println(line);
				}
			}
			bufferedreader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void replaceData(String tag, String line, ArrayList<HuffmanData> hd) {
		int pos = 0;
		for(int i = 0; i < line.length(); i++) {
			System.out.print(line.charAt(i));
			if(line.charAt(i) == '<' && line.charAt(i + 1) == '{') {
				System.out.print(line.charAt(i+1) + " (");
				pos = i + 2;
				break;
			}
		}
		
		for(int i = 0; i < hd.size(); i++) {
			String code = this.huffmanInfo.encodeMap.get(hd.get(i).term);
			String term = hd.get(i).term;
			
			System.out.print(" 0b" + code + " " + "`" + term + "`");
			if(i != hd.size()-1) {
				System.out.print("\n\t/");				
			}
			else {
				System.out.print("\n\t " + tag + " ) }>");
			}
		}
		for(int i = pos; i < line.length(); i++) {
			if(line.charAt(i) == '}' && line.charAt(i + 1) == '>') {
				pos = i + 2;
				break;
			}
		}
		for(int i = pos; i < line.length(); i++) {
			System.out.print(line.charAt(i));
		}
	}
	
	private void putDataMap(String tag, String line) {
		ArrayList<HuffmanData> hd = new ArrayList<HuffmanData>();
		hd = this.huffmanInfo.getHuffmanDataList(tag);
		if(hd.size() != 0) {
			replaceData(tag, line, hd);
		}
		else {
			System.out.println(line);
		}
		//this.huffmanInfo.showList(hd);
	}
	
	public void generatePegFile() {
		recompile();
	}
}