package org.peg4d.data;

import java.util.Comparator;

public class Point {
	private int ltpos = -1;
	private int rtpos = -1;
	private int range = -1;
	
	public Point(int ltpos, int rtpos) {
		this.ltpos = ltpos;
		this.rtpos = rtpos;
		this.range = rtpos - ltpos;
	}
	
	public int getLtPos() {
		return this.ltpos;
	}
	public int getRtPos() {
		return this.rtpos;
	}
	public int getRange() {
		return this.range;
	}
}
