package org.peg4d.data;

import org.peg4d.*;

public class SegmentTreeBuilder {
	private int pos = -1;
	public SegmentTreeBuilder() {
		this.pos = 0;
	}

	public void build(ParsingObject node) {
		if(node == null) return;
		node.setLpos(this.pos++);
		for(int i = 0; i < node.size(); i++) {
			this.build(node.get(i));
		}
		node.setRpos(this.pos++);
	}
}
