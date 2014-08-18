package org.peg4d;

import java.util.ArrayList;
import java.util.Comparator;

public class HuffmanData implements Comparator<HuffmanData> {
	String             tag;
	String             term;
	ArrayList<Boolean> code;
	int                occurence;

	public HuffmanData(String tag, String term) {
		this.tag = tag;
		this.term = term;
		this.code = new ArrayList<Boolean>();
		this.occurence = 0;
	}
	
	@Override
	public int compare(HuffmanData o1, HuffmanData o2) {
		int n1 = o1.occurence;
		int n2 = o2.occurence;
		if(n1 < n2) {
			return 1;
		}
		else if(n1 == n2) {
			return 0;
		}
		else {
			return -1;
		}
	}
}
