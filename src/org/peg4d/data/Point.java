package org.peg4d.data;


public class Point {
	final private int left;
	final private int right;

	public Point(int left, int right) {
		this.left = left;
		this.right = right;
	}

	public int getLeft() {
		return this.left;
	}

	public int getRight() {
		return this.right;
	}

	public int getRange() {
		return this.right - this.left;
	}

	public boolean contains(Point p) {
		return (p.left < this.left && this.right < p.right);
	}
}
