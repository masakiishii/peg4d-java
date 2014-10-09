package org.peg4d.data;

import org.peg4d.*;

public class BuildJeoCelkoSet {
	private static int pos = -1;
	public BuildJeoCelkoSet() {
		this.pos = 0;
	}
	
	public void numberingLRpos(ParsingObject node) {
		if(node == null) return;
		node.setLpos(this.pos++);
		for(int i = 0; i < node.size(); i++) {
			this.numberingLRpos(node.get(i));
		}
		node.setRpos(this.pos++);
	}
}
