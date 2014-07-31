package org.peg4d;

import java.util.Comparator;

public class HuffmanData implements Comparator<HuffmanData> {
	String tag;
	String source;
	String code;
	int counter;
	public HuffmanData(String tag, String source) {
		this.tag = tag;
		this.source = source;
		this.code = "";
		this.counter = 1;
	}
	public HuffmanData(String tag, String source, int counter) {
		this.tag = tag;
		this.source = source;
		this.code = "";
		this.counter = counter;
	}
	@Override
	public int compare(HuffmanData o1, HuffmanData o2) {
		int n1 = o1.counter;
		int n2 = o2.counter;
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
